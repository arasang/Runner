package park.sangeun.runner.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import park.sangeun.runner.Activity.LoginActivity;
import park.sangeun.runner.R;

/**
 * Created by user on 2017-04-06.
 */

public class UserInfoFragment extends Fragment {

    private TextInputEditText editHeight;
    private TextInputEditText editWeight;

    private String userHeight;
    private String userWeight;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_login, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_userinfo, container);
        editHeight = (TextInputEditText) v.findViewById(R.id.editHeight);
        editWeight = (TextInputEditText) v.findViewById(R.id.editWeight);

        setHasOptionsMenu(true);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            ((LoginActivity)getActivity()).goUserName();
        }

        if (item.getItemId() == R.id.actionNext) {
            int height = Integer.parseInt(editHeight.getText().toString());
            int weight = Integer.parseInt(editWeight.getText().toString());
            ((LoginActivity)getActivity()).onLogin(height, weight);
        }
        return super.onOptionsItemSelected(item);
    }
}
