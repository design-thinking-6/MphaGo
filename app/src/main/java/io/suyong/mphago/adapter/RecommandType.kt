package io.suyong.mphago.adapter

import android.view.ViewGroup

data class RecommandType(val title: String, var accept: String? = null, var layouts: MutableList<ViewGroup> = mutableListOf())