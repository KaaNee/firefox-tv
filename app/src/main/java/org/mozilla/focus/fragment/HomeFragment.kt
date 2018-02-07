/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.fragment

import android.graphics.Color
import android.os.Bundle
import android.content.Context
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONObject
import org.mozilla.focus.R
import org.mozilla.focus.autocomplete.UrlAutoCompleteFilter
import org.mozilla.focus.telemetry.TelemetryWrapper
import org.mozilla.focus.telemetry.UrlTextInputLocation
import org.mozilla.focus.utils.HomeTileUtils
import org.mozilla.focus.utils.OnUrlEnteredListener
import android.graphics.BitmapFactory

private const val COL_COUNT = 5

/** The home fragment which displays the navigation tiles of the app. */
class HomeFragment : Fragment() {
    lateinit var urlBar: LinearLayout
    var onUrlEnteredListener = object : OnUrlEnteredListener {} // default impl does nothing.
    val urlAutoCompleteFilter = UrlAutoCompleteFilter()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_home, container, false)
        urlBar = rootView.findViewById(R.id.homeUrlBar)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // todo: saved instance state?
        initTiles()
        initUrlInputView()
    }

    override fun onResume() {
        super.onResume()
        urlAutoCompleteFilter.load(context)
    }

    override fun onAttachFragment(childFragment: Fragment?) {
        super.onAttachFragment(childFragment)
        urlBar.requestFocus()
    }

    private fun initTiles() = with (tileContainer) {
        var homeTiles = mutableListOf<JSONObject>()
        val jsonArray = HomeTileUtils.loadDefaultTiles(context).getJSONArray("default_tiles")
        for (i in 0..(jsonArray.length() - 1)) {
            homeTiles.add(jsonArray.getJSONObject(i))
        }
        adapter = HomeTileAdapter(onUrlEnteredListener, homeTiles, context)
        layoutManager = GridLayoutManager(context, COL_COUNT)
        setHasFixedSize(true)
    }

    private fun initUrlInputView() = with (urlInputView) {
        setOnCommitListener {
            onUrlEnteredListener.onTextInputUrlEntered(text.toString(), urlInputView.lastAutocompleteResult, UrlTextInputLocation.HOME)
        }
        setOnFilterListener { searchText, view -> urlAutoCompleteFilter.onFilter(searchText, view) }
    }

    companion object {
        const val FRAGMENT_TAG = "home"

        @JvmStatic
        fun create() = HomeFragment()
    }
}

private class HomeTileAdapter(val onUrlEnteredListener: OnUrlEnteredListener, homeTiles: MutableList<JSONObject>, context: Context) :
        RecyclerView.Adapter<TileViewHolder>() {
    val context = context
    val tiles = listOf(
            HomeTile(homeTiles[0].getJSONObject("tile_1").getString("url"), homeTiles[0].getJSONObject("tile_1").getString("title"), homeTiles[0].getJSONObject("tile_1").getString("img")),
            HomeTile(homeTiles[1].getJSONObject("tile_2").getString("url"), homeTiles[1].getJSONObject("tile_2").getString("title"), homeTiles[1].getJSONObject("tile_2").getString("img")),
            HomeTile(homeTiles[2].getJSONObject("tile_3").getString("url"), homeTiles[2].getJSONObject("tile_3").getString("title"), homeTiles[2].getJSONObject("tile_3").getString("img")),
            HomeTile(homeTiles[3].getJSONObject("tile_4").getString("url"), homeTiles[3].getJSONObject("tile_4").getString("title"), homeTiles[3].getJSONObject("tile_4").getString("img")),

            // order?
            HomeTile(homeTiles[4].getJSONObject("tile_5").getString("url"), homeTiles[4].getJSONObject("tile_5").getString("title"), homeTiles[4].getJSONObject("tile_5").getString("img")),
            HomeTile(homeTiles[5].getJSONObject("tile_6").getString("url"), homeTiles[5].getJSONObject("tile_6").getString("title"), homeTiles[5].getJSONObject("tile_6").getString("img")),

            HomeTile(homeTiles[6].getJSONObject("tile_7").getString("url"), homeTiles[6].getJSONObject("tile_7").getString("title"), homeTiles[6].getJSONObject("tile_7").getString("img")),
            HomeTile(homeTiles[7].getJSONObject("tile_8").getString("url"), homeTiles[7].getJSONObject("tile_8").getString("title"), homeTiles[7].getJSONObject("tile_8").getString("img")),
            HomeTile(homeTiles[8].getJSONObject("tile_9").getString("url"), homeTiles[8].getJSONObject("tile_9").getString("title"), homeTiles[8].getJSONObject("tile_9").getString("img")), // sign in required
            HomeTile(homeTiles[9].getJSONObject("tile_10").getString("url"), homeTiles[9].getJSONObject("tile_10").getString("title"), homeTiles[9].getJSONObject("tile_10").getString("img")) // sign in required
    )

    override fun onBindViewHolder(holder: TileViewHolder, position: Int) = with (holder) {
        val item = tiles[position]
        titleView.setText(item.title)
        val bmImg = BitmapFactory.decodeStream(context.assets.open("defaults/" + item.imagePath))
        iconView.setImageBitmap(bmImg)
        itemView.setOnClickListener {
            onUrlEnteredListener.onNonTextInputUrlEntered(item.url)
            TelemetryWrapper.homeTileClickEvent()
        }
        itemView.setOnFocusChangeListener { v, hasFocus ->
            val backgroundResource: Int
            val textColor: Int
            if (hasFocus) {
                backgroundResource = R.drawable.home_tile_title_focused_background
                textColor = Color.WHITE
            } else {
                backgroundResource = 0
                textColor = Color.BLACK
            }
            titleView.setBackgroundResource(backgroundResource)
            titleView.setTextColor(textColor)
        }
    }

    override fun getItemCount() = tiles.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TileViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.home_tile, parent, false)
    )
}

private class TileViewHolder(
        itemView: View
) : RecyclerView.ViewHolder(itemView) {
    val iconView = itemView.findViewById<ImageView>(R.id.tile_icon)
    val titleView = itemView.findViewById<TextView>(R.id.tile_title)
}

private data class HomeTile (
        val url: String,
        val title: String,
        val imagePath: String
)
