package io.mithrilcoin.eoscommander.di.component;

import dagger.Component;
import io.mithrilcoin.eoscommander.ui.MainActivity;
import io.mithrilcoin.eoscommander.di.PerActivity;
import io.mithrilcoin.eoscommander.di.module.ActivityModule;
import io.mithrilcoin.eoscommander.ui.account.AccountMainFragment;
import io.mithrilcoin.eoscommander.ui.account.create.CreateEosAccountDialog;
import io.mithrilcoin.eoscommander.ui.account.info.InputAccountDialog;
import io.mithrilcoin.eoscommander.ui.settings.SettingsActivity;
import io.mithrilcoin.eoscommander.ui.gettable.GetTableFragment;
import io.mithrilcoin.eoscommander.ui.push.PushFragment;
import io.mithrilcoin.eoscommander.ui.transfer.TransferFragment;
import io.mithrilcoin.eoscommander.ui.wallet.WalletFragment;
import io.mithrilcoin.eoscommander.ui.wallet.dlg.CreateWalletDialog;
import io.mithrilcoin.eoscommander.ui.wallet.dlg.InputDataDialog;

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
    void inject(TransferFragment fragment);

    void inject(InputDataDialog dialog);
    void inject(CreateWalletDialog dialog);

    void inject(InputAccountDialog dialog);
}
