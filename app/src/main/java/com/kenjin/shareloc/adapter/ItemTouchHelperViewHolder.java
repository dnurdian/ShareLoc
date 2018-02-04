package com.kenjin.shareloc.adapter;

/**
 * Created by kenjin on 02/02/18.
 */

interface ItemTouchHelperViewHolder {
    /**
     * Implementations should update the item view to indicate it's active state.
     */
    void onItemSelected();


    /**
     * state should be cleared.
     */
    void onItemClear();
}
