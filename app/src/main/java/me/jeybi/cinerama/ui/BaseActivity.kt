package me.jeybi.cinerama.ui

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity(){


    /* Here there is not any reason or advantage of creating BASEACTIVITY yet
        However as Application gets better and LOCALIZATION , THEMING , DAGGER , ROOM and all other
        tools are added, it will reduce pretty much amount of code and prevent you from repeating the same code
    */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(setLayoutId())
        onViewDidCreate(savedInstanceState)
    }

    abstract fun onViewDidCreate(savedInstanceState: Bundle?)
    @LayoutRes abstract fun setLayoutId() : Int
}