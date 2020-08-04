package com.erastus.orientate.utils.reaction


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point

@SuppressLint("ViewConstructor")
class ReactionView constructor(
        context: Context,
        val reaction: Reaction
) : androidx.appcompat.widget.AppCompatImageView(context) {

    val location = Point()
        get() {
            if (field.x == 0 || field.y == 0) {
                val location = IntArray(2).also(::getLocationOnScreen)
                field.set(location[0], location[1])
            }
            return field
        }

    init {
        scaleType = reaction.scaleType
        setImageDrawable(reaction.image)
    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        location.set(0, 0)
    }
}
