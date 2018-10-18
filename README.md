# EOS commander for developer 

<p align="center">
  <img src="https://github.com/playerone-id/files/blob/master/eos_commander/eosc_icon.png?raw=true">
</p>

EOS commander is an Android client for EOSIO DApp developer.  
It includes functions of wallet. Developers can test wallet, account, transaction, contract, etc. on the Android device.
  
EOS Commander is designed with MVP pattern.  
You can use "data" and "crypto" package to any JAVA projects.    
 
### PlayerOne is new name of Plactal.

# Table of contents
- [Getting Started](#getting_started)
- [Set connection](#set_connection)
- [Wallet](#wallet)
- [Account](#account)
- [Transfer](#transfer)
- [Currency](#currency)
- [Push](#push)
- [Get table](#get_table)
- [Apps using EOS Commander code](#using_code)
- [About PlayerOne](#about_playerone)
- [Contact](#contact) 
- [License](#license)

<a name="getting_started"></a>
## Getting Started
### Prerequisite


For Testing on private net:  
You should have running nodeos node.  
Set "http-server-address" other than "127.0.0.1" .  
EOS commander includes wallet function, you don't need to specify "wallet_api_plugin" in config.ini.

EOS Commander has been tested with EOSIO version [1.3.2](https://github.com/EOSIO/eos/tree/v1.3.2).

See [EOSIO github](https://github.com/EOSIO/eos).

### Build
On the console type:

	git clone https://github.com/playerone-id/eoscommander.git

Open in Android studio 3.0 or later.

### Install from Play Store
  
You can install the latest version from the Play store at: [link](https://play.google.com/store/apps/details?id=io.plactal.eoscommander)

Or download apk : [releases](https://github.com/playerone-id/EosCommander/releases)  



<a name="set_connection"></a>
## Set connection  

For Private test net:  
You should have running nodeos node.  
Set "http-server-address" other than "127.0.0.1".  

### Connect
Connect to eos network and check the status.
On connected state, you can use the command function.

<a name="getting_started"></a>
## Wallet
### create default wallet and import key of eosio.
This creates a wallet named 'default' and import the private key of `eosio` account.

### create wallet
You can also create wallets other than "default".
Also provides option for saving password for easy testing.
(This makes password automatically filled when you unlock a wallet.)

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
Transfer eos. (Push transfer message on built-in `eosio` smart contract)

## Currency
Run "get balance" or "get stats" commands for currency contract.

<a name="push"></a>
## Push
Push the contract message.
[See EOSIO Developer Portal](https://developers.eos.io/eosio-cleos/reference#cleos-push-action)  
You can type json manually, or edit via form input UI( after reading ABI from EOS network), or importing JSON file.  

<a name="get_table"></a>
## Get table
Lists the contract table.
[See EOSIO Developer Portal](https://developers.eos.io/eosio-cleos/reference#cleos-get-table)

<a name="using_code"></a>
## Apps using EOS Commander source
Please [ping](mailto:eric.song@playerone.id) me or send a pull request if you would like to be added here.  

[PocketEOS-Android]( https://github.com/OracleChain/PocketEOS-Android ) by orcalechain.io


<a name="about_playerone"></a>
## About PlayerOne

[PlayerOne](https://playerone.id) is a decentralized gamer identity protocol. 

<a name="contact"></a>
## Contact
eric.song@playerone.id
  
<a name="lincense"></a>
## License

    Copyright (c) 2017-2018 PlayerOne.

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

