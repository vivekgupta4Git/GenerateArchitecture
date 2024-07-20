package com.ruviapps.androidcalm

import com.squareup.kotlinpoet.FileSpec

/**
 *@author Vivek Gupta on 18-7-24
 */
fun generateFile(packageName : String,fileName : String){
    val code = FileSpec.builder(packageName,fileName).build()
    code.writeTo(System.out)
}