package retailworks.in.field.app;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import retailworks.in.field.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddVisitorFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddVisitorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddVisitorFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    View rootView = null;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddVisitorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddVisitorFragment newInstance(String param1, String param2) {
        AddVisitorFragment fragment = new AddVisitorFragment();
        return fragment;
    }


    public AddVisitorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_add_visitor, container, false);

        String[] shopNames = new String[]{""};
        String[] coNames = new String[]{""};

        ArrayAdapter<String> storeAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,shopNames);
        storeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner stores = (Spinner) rootView.findViewById(R.id.storeSpinner);
        stores.setAdapter(storeAdapter);


        ArrayAdapter<String> coAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, coNames);
        coAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner co = (Spinner) rootView.findViewById(R.id.coSpinner);
        co.setAdapter(coAdapter);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
}
