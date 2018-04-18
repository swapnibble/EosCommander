package io.plactal.eoscommander.di.component;

import dagger.Component;
import io.plactal.eoscommander.ui.MainActivity;
import io.plactal.eoscommander.di.PerActivity;
import io.plactal.eoscommander.di.module.ActivityModule;
import io.plactal.eoscommander.ui.account.AccountMainFragment;
import io.plactal.eoscommander.ui.account.create.CreateEosAccountDialog;
import io.plactal.eoscommander.ui.account.info.InputAccountDialog;
import io.plactal.eoscommander.ui.currency.CurrencyFragment;
import io.plactal.eoscommander.ui.push.abiview.MsgInputActivity;
import io.plactal.eoscommander.ui.settings.SettingsActivity;
import io.plactal.eoscommander.ui.gettable.GetTableFragment;
import io.plactal.eoscommander.ui.push.PushFragment;
import io.plactal.eoscommander.ui.transfer.TransferFragment;
import io.plactal.eoscommander.ui.wallet.WalletFragment;
import io.plactal.eoscommander.ui.wallet.dlg.CreateWalletDialog;
import io.plactal.eoscommander.ui.wallet.dlg.InputDataDialog;

/**
 * Created by swapnibble on 2017-08-24.
 */
@PerActivity
@Component( dependencies = AppComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity activity);
    void inject(SettingsActivity activity);

    void inject(AccountMainFragment fragment);
    void inject(CreateEosAccountDialog dialog);

    void inject(WalletFragment fragment);
    void inject(PushFragment fragment);
    void inject(GetTableFragment fragment);

    void inject(CurrencyFragment fragment);
    void inject(TransferFragment fragment);

    void inject(InputDataDialog dialog);
    void inject(CreateWalletDialog dialog);

    void inject(InputAccountDialog dialog);

    void inject(MsgInputActivity activity);
}
