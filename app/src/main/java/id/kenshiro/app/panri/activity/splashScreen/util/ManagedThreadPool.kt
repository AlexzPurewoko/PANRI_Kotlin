package id.kenshiro.app.panri.activity.splashScreen.util

import java.lang.ref.WeakReference
import java.util.concurrent.*


class ManagedThreadPool private constructor() : BgThreadFactoryCallback {


    private val mExecutorThreadService: ThreadPoolExecutor
    private val mTaskQueue: BlockingQueue<Runnable>
    private var mRunningTaskQueue: MutableList<Future<*>>
    private var mBgFactoryCallback: WeakReference<BgThreadFactoryCallback>? = null

    companion object {
        private val numberOfCores = Runtime.getRuntime().availableProcessors()
        private val keepAliveTime: Long = 1
        private val keepAliveTimeUnit = TimeUnit.SECONDS
        private val sInstance: ManagedThreadPool = ManagedThreadPool()
        fun getInstance(): ManagedThreadPool {
            return sInstance
        }
    }

    init {
        mTaskQueue = LinkedBlockingQueue<Runnable>()
        mRunningTaskQueue = ArrayList()
        mExecutorThreadService = ThreadPoolExecutor(
            2,
            numberOfCores * 2,
            keepAliveTime,
            keepAliveTimeUnit,
            mTaskQueue,
            BackgroundThreadFactory(this)
        )
    }

    override fun onBuildThread(source: Thread, runnable: Runnable?) {
        mBgFactoryCallback?.get()?.onBuildThread(source, runnable)
    }

    override fun onUncaughtException(source: Thread, throwable: Throwable) {
        mBgFactoryCallback?.get()?.onUncaughtException(source, throwable)
    }


    fun addRunnable(callable: Runnable) {
        val future = mExecutorThreadService.submit(callable)
        mRunningTaskQueue.add(future)
    }

    fun isAllTaskFinished(): Boolean {
        return mExecutorThreadService.activeCount == 0
    }

    fun cancelAllTasks() {
        synchronized(this) {
            mTaskQueue.clear()
            for (task in mRunningTaskQueue) {
                if (!task.isDone)
                    task.cancel(true)
            }
            mRunningTaskQueue.clear()
        }
    }

    fun setBgThreadCallback(bgCallback: BgThreadFactoryCallback) {
        mBgFactoryCallback = WeakReference(bgCallback)
    }

    private class BackgroundThreadFactory(val bgCallback: BgThreadFactoryCallback?) : ThreadFactory {
        override fun newThread(r: Runnable?): Thread {
            val thread = Thread(r)
            thread.priority = android.os.Process.THREAD_PRIORITY_BACKGROUND
            thread.setUncaughtExceptionHandler { t, e ->
                bgCallback?.onUncaughtException(t, e)

            }
            bgCallback?.onBuildThread(thread, r)
            return thread

        }

    }


}

interface BgThreadFactoryCallback {
    fun onBuildThread(source: Thread, runnable: Runnable?)
    fun onUncaughtException(source: Thread, throwable: Throwable)
}