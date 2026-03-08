package com.interview.rickandmortyturkcell.ui.photodetail.model

sealed interface UiMessage {
    data class Resource(val resId: Int) : UiMessage
    data class Dynamic(val value: String) : UiMessage
}