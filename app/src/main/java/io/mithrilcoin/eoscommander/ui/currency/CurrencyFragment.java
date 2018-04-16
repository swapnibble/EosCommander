package io.mithrilcoin.eoscommander.ui.currency;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatSpinner;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import javax.inject.Inject;

import io.mithrilcoin.eoscommander.R;
import io.mithrilcoin.eoscommander.di.component.ActivityComponent;
import io.mithrilcoin.eoscommander.ui.base.BaseFragment;
import io.mithrilcoin.eoscommander.ui.widget.TextInputAutoCompleteTextView;
import io.mithrilcoin.eoscommander.util.UiUtils;

/**
 * Created by swapnibble on 2018-04-16.
 */
public class CurrencyFragment extends BaseFragment implements CurrencyMvpView {
    // belows are position of spinner
    private static final int POSITION_GET_BALANCE = 0;
    private static final int POSITION_GET_STATS = 1;

    @Inject
    CurrencyPresenter mPresenter;

    private AppCompatSpinner mCmdSpinner;
    private TextInputLayout  mTilAccount;
    private AutoCompleteTextView   mEtContract;
    private AutoCompleteTextView    mEtAccount;
    private EditText    mEtSymbol;


    private int mCurSelectedPosition = -1;

    public static CurrencyFragment newInstance() {
        CurrencyFragment fragment = new CurrencyFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_currency, container, false);
        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            mPresenter.attachView( this );
        }

        return view;
    }

    @Override
    protected void setUpView(View view) {

        mEtContract = view.findViewById( R.id.et_token_contract);
        mEtAccount  = view.findViewById( R.id.et_account);
        mEtSymbol   = view.findViewById( R.id.et_symbol);

        mTilAccount = view.findViewById(R.id.til_account);
        mCmdSpinner = view.findViewById(R.id.sp_currency_cmd);

        mEtSymbol.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if ( EditorInfo.IME_ACTION_SEND == actionId ) {
                sendCommand();
                return true;
            }

            return false;
        });

        // data position should be matched to POSITION_xxx constants
        ArrayAdapter<String> adapter = new ArrayAdapter<>( getContext(), android.R.layout.simple_spinner_item
                , new String[]{ getString( R.string.get_balance), getString(R.string.get_stats)} );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCmdSpinner.setAdapter( adapter );

        mCmdSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                onCmdSelected( position );
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });


        view.findViewById(R.id.btn_get).setOnClickListener( v -> sendCommand());


        // setup account history
        UiUtils.setupAccountHistory( mEtContract, mEtAccount);
    }

    private void onCmdSelected( int position ) {
        hideKeyboard();

        // position == 0 -> get balance..
        mTilAccount.setVisibility( ( position == POSITION_GET_BALANCE ) ? View.VISIBLE : View.GONE);

        mCurSelectedPosition = position;
    }

    private void sendCommand(){
        if ( POSITION_GET_BALANCE == mCurSelectedPosition) {
            mPresenter.onGetBalance( mEtContract.getText().toString(), mEtAccount.getText().toString(), mEtSymbol.getText().toString());
        }
        if ( POSITION_GET_STATS == mCurSelectedPosition) {
            mPresenter.onGetStats( mEtContract.getText().toString(), mEtSymbol.getText().toString());
        }
    }

}
