package com.mizzugi.kensiro.app.panri.activity

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Point
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.navigation.NavigationView
import com.mizzugi.kensiro.app.panri.BaseActivity
import com.mizzugi.kensiro.app.panri.R
import com.mizzugi.kensiro.app.panri.fragment.ImageFarmerAnimations
import com.mizzugi.kensiro.app.panri.fragment.imageSlider.ImageSwipeFragmentAdapter
import com.mizzugi.kensiro.app.panri.plugin.DialogPlugin
import com.mizzugi.kensiro.app.panri.plugin.LoadingDialogPlugin
import com.mizzugi.kensiro.app.panri.plugin.PublicContract
import com.mizzugi.kensiro.app.panri.plugin.SwitchActivity
import com.mizzugi.kensiro.app.panri.plugin.onMain.Modules
import com.mizzugi.kensiro.app.panri.plugin.onMain.Modules.validateVersion
import com.mizzugi.kensiro.app.panri.viewmodel.CheckManualUpdateDBViewModel
import com.mizzugi.kensiro.app.panri.viewmodel.TaskManualUpdateDBViewModel
import id.apwdevs.library.text.style.CustomTypefaceSpan
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mDialogLoadingDialogPlugin: LoadingDialogPlugin

    private lateinit var manualCheckUpdate: CheckManualUpdateDBViewModel
    lateinit var taskDownloadDBUpdateDBViewModel: TaskManualUpdateDBViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setupDrawerToolbar()
        // load all Frame <Fragment>
        loadAllFrame()
        setupIntent()
        checkDBManual()
        validateVersion(this)

    }

    private fun setupIntent() {
        Modules.initializeSectOptIntent(this, actmain_id_listmainoperation)
    }

    private fun setupDrawerToolbar() {
        toolbar.title = SpannableString(title).apply {
            setSpan(
                CustomTypefaceSpan(Typeface.createFromAsset(assets, "Gecko_PersonalUseOnly.ttf")),
                0,
                title.length,
                0
            )
        }
        setSupportActionBar(toolbar)

        drawer_layout.apply {
            val toggle = ActionBarDrawerToggle(
                this@MainActivity,
                this,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            )
            addDrawerListener(toggle)
            toggle.syncState()
        }
        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun loadAllFrame() {
        // frame 1 -> View Pager
        val point = Point()
        windowManager.defaultDisplay.getSize(point)
        val frame1 = ImageSwipeFragmentAdapter.newInstance(point, 6000)
        supportFragmentManager.beginTransaction().replace(R.id.container_frame_viewpager, frame1)
            .commit()

        // frame 2 -> section farmer
        val frame2 = ImageFarmerAnimations.newInstance(6000, 3000)
        supportFragmentManager.beginTransaction().replace(R.id.container_frame_farmer, frame2)
            .commit()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (drawer_layout.isDrawerOpen(nav_view))
                drawer_layout.closeDrawer(nav_view, true)
            else
                DialogPlugin.showExitAppDialog(this)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.nav_gallery -> {
                SwitchActivity.switchTo(
                    this,
                    GalleryActivity::class.java,
                    null
                )//GalleryActivity::class.java, null)
            }

            R.id.nav_about -> {
                SwitchActivity.switchTo(
                    this,
                    AboutActivity::class.java,
                    null
                )//AboutActivity::class.java, null)
            }

            R.id.nav_out -> {
                DialogPlugin.showExitAppDialog(this)
            }
            R.id.update_db -> {
                manualCheckUpdate.check()
            }
            R.id.nav_rate -> {
                val packageApp = packageName.toString()
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse("https://play.google.com/store/apps/details?id=$packageApp")
                i.setPackage("com.android.vending")
                startActivity(Intent.createChooser(i, "Beri Nilai dengan..."))
            }
            R.id.nav_share -> {
                val i = Intent(Intent.ACTION_SEND)
                i.putExtra(
                    Intent.EXTRA_TEXT,
                    String.format(
                        "Download Aplikasi PANRI ke smartphone Android kamu dengan klik https://play.google.com/store/apps/details?id=%s",
                        packageName.toString()
                    )
                )
                i.type = "text/plain"
                startActivity(Intent.createChooser(i, "Bagikan Aplikasi dengan..."))
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {
            SwitchActivity.switchTo(this, PanriSettingActivity::class.java, null)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SetTextI18n", "InflateParams")
    private fun checkDBManual() {
        mDialogLoadingDialogPlugin = LoadingDialogPlugin(this)
        mDialogLoadingDialogPlugin.buildLoadingLayout()
        //mDialogLoadingDialogPlugin.showDialog()
        taskDownloadDBUpdateDBViewModel =
            ViewModelProviders.of(this).get(TaskManualUpdateDBViewModel::class.java)
        taskDownloadDBUpdateDBViewModel.apply {
            var textProgress: TextView
            val progressBar: ProgressBar
            val builder = AlertDialog.Builder(this@MainActivity).apply {
                val rootElement =
                    LayoutInflater.from(context).inflate(R.layout.dialog_layout_progress, null)
                        .apply {
                            textProgress = findViewById(R.id.loading_progress_text)
                            progressBar = findViewById(R.id.loading_progress_bar)
                            progressBar.max = 100
                            progressBar.progress = 0
                            textProgress.text = "Mempersiapkan..."
                        }

                setView(rootElement)
                setCancelable(false)
            }.create()

            this.finishedLoading.observe(this@MainActivity, Observer {
                if (!it) {
                    builder.show()
                } else {
                    builder.dismiss()
                    if (isCompleted)
                        AlertDialog.Builder(this@MainActivity).apply {
                            setIcon(R.mipmap.ic_launcher)
                            setTitle("Restart")
                            setCancelable(false)
                            setMessage("Perubahan telah disimpan, Klik tombol 'restart' untuk me-restart aplikasi untuk mengaplikasikan perubahan yang telah dilakukan")
                            setPositiveButton(
                                "Restart"
                            ) { dialog, _ ->
                                dialog.dismiss()
                                startActivity(
                                    this@MainActivity.packageManager
                                        .getLaunchIntentForPackage(packageName)?.apply {
                                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                        }
                                )
                            }
                        }.show()
                }
            })

            this.currentProgress.observe(this@MainActivity, Observer {
                textProgress.text = "${this.stateText}...$it %"
                progressBar.progress = it
            })
        }

        manualCheckUpdate =
            ViewModelProviders.of(this).get(CheckManualUpdateDBViewModel::class.java)
        manualCheckUpdate.apply {
            manualCheckUpdate.finishedLoading.observe(this@MainActivity, Observer {
                if (!it) {
                    mDialogLoadingDialogPlugin.showDialog()
                } else {
                    mDialogLoadingDialogPlugin.stopDialog()

                    when (this.message) {
                        PublicContract.UPDATE_DB_IS_AVAILABLE -> {
                            DialogPlugin.showUpdateDBDialog(this@MainActivity, message,
                                oldDBVersion,
                                newDBVersion,
                                "Update",
                                object : DialogInterface.OnClickListener {

                                    override fun onClick(dialog: DialogInterface?, which: Int) {
                                        dialog?.dismiss()
                                        newDBVersion?.let { nVer ->
                                            taskDownloadDBUpdateDBViewModel.run(nVer)
                                        }
                                        //new TaskDownloadDBUpdates(actReference.get(), fetchedVerson[1]).execute();
                                    }
                                }
                            )
                        }
                        PublicContract.UPDATE_DB_NOT_AVAILABLE, PublicContract.UPDATE_DB_NOT_AVAILABLE_INTERNET_MISSING -> {
                            DialogPlugin.showUpdateDBDialog(this@MainActivity, message)
                        }
                    }
                }
            })


        }
    }

}
