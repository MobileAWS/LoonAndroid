package com.maws.loonandroid.ifaces;

import java.util.List;

/**
 * Created by Andrexxjc on 27/03/2015.
 */
public interface MultipleSelectionAdapter<T> {

    public List<T> getSelectedItems();

    public void selectItem(int position);

    public void unselectItem(int position);

    public void selectAll();

    public void toogleItem(int position);
}
