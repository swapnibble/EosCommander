# EOS commander for developer 

EOS commander is an Android client for EOS blockchain for EOS DApp developer. 
It includes functions of wallet. Developers can test wallet, account, transaction, contract, etc. with simple input on the Android device. Mithrilcoin team hopes this makes more EOS mobile DApps activated. 

# Table of contents
- [Getting Started](#getting_started)
- [Set connection](#set_connection)
- [Wallet](#wallet)
- [Account](#account)
- [Transfer](#transfer)
- [Push](#push)
- [Get table](#get_tabel)
- [About Mithrilcoin](#about_mithrilcoin)
- [License](#license)

<a name="getting_started"></a>
## Getting Started
### Prerequisite
You shoud have running eosd node.  
Set "http-server-address" other than "127.0.0.1" in config.ini.  
EOS commander includes wallet function, you don't need to specify "wallet_api_plugin" in config.ini.

EOS Commander has been tested with EOS version [bb32369](https://github.com/EOSIO/eos/tree/bb3236923bc392915061b07152474e4bf9fc9b60).

See [EOSIO github](https://github.com/EOSIO/eos).

### Build
On the console type:

	git clone https://github.com/mithrilcoin-io/eoscommander.git

Open in Android studio 3.0 or later.

### Install from Play Store
You can install the latest version from the Play store at: [link](https://play.google.com/store/apps/details?id=io.mithrilcoin.eoscommander)

<a name="set_connection"></a>
## Set connection
### Connect
Connect to eosd and check the status.
On connected state, you can use the command function.

### Skipping signature
You can run commands without signing. But in this case, the eosd must have been started with "--skip-transaction-signatures" switch.
[See EOS README](https://github.com/EOSIO/eos/blob/master/README.md#localtestnet)

<a name="getting_started"></a>
## Wallet
### create default wallet and import key of inita.
This creates a wallet named 'default' and import the private key of `inita` account.
[See EOS README](https://github.com/EOSIO/eos/blob/master/README.md#walletimport)
### create wallet
You can also create wallets other than "default".
Also provides option for saving password for easy testing.
(This makes password filled automatically when you unlock a wallet.)

### view keys
Lists imported private keys and their respective public key.
### Import key
Import key to sign the transaction to wallet.
### Lock / Unlock
Lock or unlock wallet.

<a name="account"></a>
## Account
### create account
Create the account with these characters : 'a-z' or '12345' or '.'(dot) .
You should unlock a wallet to save keys.
### get account
View the current account status.
### get transactions
Query list of transactions.
### get servants
Query the controlled_account.
<a name="set_connection"></a>
## Transfer
Transfer eos. (Push transfer message on built-in `eos` smart contract)

<a name="push"></a>
## Push
Push the contract message.
[See EOS README](https://github.com/EOSIO/eos/blob/master/README.md#pushamessage)

<a name="get_table"></a>
## Get table
Lists the contract table.
[See EOS README](https://github.com/EOSIO/eos/blob/master/README.md#readingcontract)

<a name="about_mithrilcoin"></a>
# About mithrilcoin.io
[Mithril](https://mithrilcoin.io) is a decentralized mobile game ad platform. And runs on EOS network as smart contract. Mithril aims to dramatically improve ad efficiency by eliminating middlemen intervention and connecting game devs and gamers directly based on game data.

<a name="lincense"></a>
## License

    Copyright 2017 Mithril coin.

    The MIT License

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.

