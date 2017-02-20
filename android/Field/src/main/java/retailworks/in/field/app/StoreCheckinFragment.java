package retailworks.in.field.app;

/**
 * Created by Neiv on 10/19/2015.
 */

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import retailworks.in.field.R;

public class StoreCheckinFragment
        extends Fragment implements AdapterView.OnItemClickListener {

    private static ContentValues storeValues = null;
    private OnCheckinFragmentListener mListener;

    public static StoreCheckinFragment newInstance(ContentValues values) {
        storeValues = values;
        return new StoreCheckinFragment();
    }

    public void onAttach(Activity paramActivity)
    {
        super.onAttach(paramActivity);
        try {
            this.mListener = ((OnCheckinFragmentListener)paramActivity);
            return;
        }
        catch (ClassCastException localClassCastException)
        {
            throw new ClassCastException(paramActivity.toString() + " must implement OnCheckinFragmentListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_checkin, container, false);

        return root;
    }

    public void onDetach() {

        super.onDetach();
        this.mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mListener.onCheckin(position);
    }

    public static abstract interface OnCheckinFragmentListener
    {
        public abstract void onCheckin(int position);
    }
}
