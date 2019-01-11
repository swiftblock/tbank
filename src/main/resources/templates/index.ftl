<!DOCTYPE html>
<html lang="en">
<head>
    <#import "/spring.ftl" as spring />
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="chrome=1,IE=edge"/>
    <meta charset="UTF-8">
    <title>T-Bank</title>
    <script type="text/javascript" src="/js/jquery.min.js"></script>
    <link rel="stylesheet" href="css/main.css" type="text/css"/>
    <link rel="stylesheet" href="css/customOrder.css" type="text/css"/>
    <script src="https://cdn.jsdelivr.net/npm/vue@2.5.21/dist/vue.js"></script>
    <script src="https://unpkg.com/axios/dist/axios.min.js"></script>
    <script src="/js/decimal.min.js"></script>

    <link rel="stylesheet" href="https://unpkg.com/element-ui/lib/theme-chalk/index.css">
    <script src="https://unpkg.com/element-ui/lib/index.js"></script>
</head>
<body class="mainbody">

<div id="app" class="hide" :class="{show: isLoaded}" v-loading.fullscreen.lock="fullscreenLoading">
    <div class="popup-take" v-if="showTakePopup">
        <div class="popup">
        </div>
        <div class="prompt">
            <div class="promptPhoto">
                <div class="promptWritten"><@spring.message "common.tip" /></div>
                <div class="promptLong"><@spring.message "resource.pay.tip.content" />
                </div>
                <div class="attention"><@spring.message "resource.pay.tip.attention" /></div>
            </div>
            <div class="operationText" @click="closeTakePopup"><@spring.message "common.cancel" /></div>
            <div class="operation" @click="take"><@spring.message "resource.take" /></div>
        </div>
    </div>
    <div class="popup-tip" v-if="showTipPopup">
        <div class="popup">
        </div>
        <div class="prompt">
            <div class="promptPhoto">
                <div class="promptWritten"><@spring.message "common.tip" /></div>
                <div class="promptLong"><br><br>{{ tipMessage }}
                </div>
            </div>
            <div class="operation" @click="closeTipPopup"><@spring.message "common.confirm" /></div>
        </div>
    </div>

    <div class="headPhoto">
        <div class="Title">T-Bank</div>

        <div class="howUse" @click="redirect('/help')">
            <img class="helpImp" src="img/help@2x.png">
            <span class="helpText"><@spring.message "common.help" /></span>
        </div>

        <div class="record" style="border-bottom: 1px solid #fff;padding-bottom:2px;" @click="redirect('/order/history')"><@spring.message "common.order.list" /></div>
        <div class="cashPool"><@spring.message "resource.funds.pool" /></div>
        <div class="money">{{ resourceInfo.availableFunds }}
            <span style="font-size:24px;font-family:MicrosoftYaHei;font-weight:400;color:rgba(255,254,254,1)">TRX
          </span>
        </div>
    </div>
    <div class="tabSwitching">
        <div id="tab-package" class="tab-package" :class="{selected: resourceTab == 'package'}" @click="changeTab('package')"><@spring.message "resource.package" /></div>
        <div id="tab-custom" class="tab-custom" :class="{selected: resourceTab == 'custom'}" @click="changeTab('custom')"><@spring.message "resource.custom" /></div>
    </div>

    <div id="resource-package" v-show="resourceTab == 'package'">
        <div class="entry" v-for="(package, index) in resourceInfo.packageList">
            <div class="lable"></div>
            <div class="entryLeft">
                <div class="rent"><@spring.message "resource.rent.amount" />:<span class="value">{{ package.freezeAmount }}TRX</span></div>
                <div class="rent"><@spring.message "resource.rent.time" />:<span class="value">{{ package.freezeInterval }}天</span></div>
                <div class="enery">{{ package.resourceAmount }} Energy</div>

            </div>
            <div class="entryRight" @click="openTakePopup(index)">
                <div class="payValue"><span style="font-size:32px;">{{ package.payAmount }}</span>{{ package.payCurrency }}</div>
                <div class="entryButton"><@spring.message "common.buy" /></div>
            </div>
        </div>
    </div>

    <div class="mainbodydiv" id="resource-custom" v-show="resourceTab == 'custom'">
        <#--<div class="account">-->
            <#--<div class="accountTitle">-->
                <#--<div class="accountText">本地钱包</div>-->
                <#--<div class="accountSwatch"><img class="accountSwatchImg" :src="enableLocalWallet ? 'img/swatch6@2x.png' : 'img/swatch7@2x.png'" @click="switchLocalWallet"></div>-->
            <#--</div>-->
            <#--<hr class="accountLine">-->
            <#--<div class="accountValue">-->
                <#--<input class="accountValueInput" :class="{accountValueInputDisable: !enableLocalWallet}" v-model="localWalletAddress" :disabled="!enableLocalWallet"/>-->
            <#--</div>-->
        <#--</div>-->
        <div class="parameter">
            <div class="parameterTitle"><@spring.message "resource.rent.amount" /></div>
            <div class="parameterValue">
                <span class="trxValue">{{ takeCustom.freezeAmount }}</span>
                <span class="trxUnit">Trx  </span>
                <span class="trxUntSeparate">| </span>
                <span class="trxDays">{{ takeCustom.freezeInterval }}</span>
                <span class="trxDay"><@spring.message "common.resource.rent.time.unit" /></span>
            </div>
            <div class="energyValueDiv">
                <span class="energyValue">{{ customResourceAmount }} Energy</span>
                <div class="trxSlider">
                    <el-slider v-model="takeCustom.freezeAmount" :min="resourceInfo.minTrxFreezeAmount" :max="resourceInfo.maxTrxFreezeAmount" ></el-slider>
                </div>
                <div class="trxSliderLimit">
                    <span class="trxSliderMin">{{ resourceInfo.minTrxFreezeAmount }}<@spring.message "common.resource.unit" />TRX</span>
                    <span class="trxSliderMax">{{ resourceInfo.maxTrxFreezeAmount }}<@spring.message "common.resource.unit" />TRX</span>
                </div>

                <div class="daysSlider">
                    <el-slider v-model="takeCustom.freezeInterval" :min="resourceInfo.minTrxFreezeInterval" :max="resourceInfo.maxTrxFreezeInterval" ></el-slider>
                </div>
                <div class="daysSliderLimit">
                    <span class="daysSliderMin">{{ resourceInfo.minTrxFreezeInterval }}<@spring.message "common.resource.rent.time.unit" /></span>
                    <span class="daysSliderMax">{{ resourceInfo.maxTrxFreezeInterval }}<@spring.message "common.resource.rent.time.unit" /></span>
                </div>
            </div>
            <div style="margin:20px 0 50px 0;"></div>

        </div>
        <div class="paymentbutton"><span class="paymentbuttontext" @click="openTakePopup"><@spring.message "common.pay" />{{ customPayAmount }}{{ takeCustom.payCurrency }}</span></div>
    </div>
</div>

</body>
</html>
<script type="text/javascript">
var waitForGlobal = async () =>{
    // 1. check variable, 检查tronweb是否已经加载
    if (window.tronWeb) {
        let tronWeb = window.tronWeb;
        // 2. check node connection，检查所需要的API是否都可以连通
        const nodes = await tronWeb.isConnected();
        const connected = !Object.entries(nodes).map(([name, connected]) => {
            if (!connected) {
                console.log('Error: ' + name + ' is not connected');
            }
            return connected;
        }).includes(false);
        if (connected){
            // 3. 如果一切正常，启动应用。
            init();
        } else {
            console.log(`Error: TRON node is not connected`);
            console.log('wait for tronLink');
            setTimeout(async () => {
                await waitForGlobal();
            }, 100);
        }

    } else {
        // 如果检测到没有注入tronWeb对象，则等待100ms后重新检测
        console.error('wait for tronLink');
        setTimeout(async () => {
            await waitForGlobal();
        }, 100);
    }
};

waitForGlobal().then();


function init() {
    var vm = new Vue({
        el: '#app',
        data: {
            isLoaded: false,
            fullscreenLoading: false,
            resourceInfo: {},
            resourceTab: 'package',
            showTakePopup: false,
            showTipPopup: false,
            enableLocalWallet: false,
            tipMessage: '',
            localWalletAddress: '',
            refreshTime: 0,
            takeInfo: {},
            takeCustom: {
                payCurrency: 'TRX'
            },
            takePackageIndex: ''
        },
        methods: {
            changeTab: function (tabName) {
                this.resourceTab = tabName;
                this.takeInfo = {};
            },
            redirect: function (url) {
                location.href = url;
            },
            openTakePopup: function (index) {
                if (index >= 0) {
                    this.takeInfo = this.resourceInfo.packageList[index];
                } else {
                    this.takeInfo = {
                        freezeAmount: this.takeCustom.freezeAmount,
                        freezeInterval: this.takeCustom.freezeInterval,
                        payAmount: this.customPayAmount,
                        payCurrency: this.takeCustom.payCurrency,
                        resourceAmount: this.customResourceAmount
                    };
                }
                this.takeIndex = index;
                if (this.takeInfo.payAmount > 0) {
                    this.showTakePopup = true;
                }
            },
            closeTakePopup: function () {
                this.showTakePopup = false;
            },
            openTipPopup: function () {
                this.showTipPopup = true;
            },
            closeTipPopup: function () {
                this.showTipPopup = false;
            },
            switchLocalWallet: function () {
                if (this.enableLocalWallet) {
                    this.enableLocalWallet = false;
                    this.localWalletAddress = '';
                } else {
                    this.enableLocalWallet = true;
                }
            },
            loadData: function () {
                axios.post('/resource/info')
                        .then(function (response) {
                            vm.resourceInfo = response.data.data;
                            vm.refreshTime = Date.now();
                            vm.isLoaded = true;
                        }).catch(function (error) {
                    console.log(error);
                });
            },
            take: async function () {
                this.closeTakePopup();

                if (Date.now() - this.refreshTime >= this.resourceInfo.restTime) {
                    this.tipMessage = '<@spring.message "resource.package.expired" />';
                    this.openTipPopup();
                    this.loadData();
                    return;
                }

                var _this = this;
                _this.fullscreenLoading = true;

                try {
                    // 生成交易
                    var address = tronWeb.defaultAddress.base58;
                    var realPayAmount = new Decimal(this.takeInfo.payAmount).times(1000000).toFixed(0);
                    var tx = await tronWeb.transactionBuilder.sendTrx(this.resourceInfo.receiveAddress, realPayAmount);
                    tx = await tronWeb.trx.sign(tx);

                    var param = {
                        userAddress: address,
                        resourceType: 'ENERGY',
                        resourceAmount: this.takeInfo.resourceAmount,
                        trxFreezeAmount: this.takeInfo.freezeAmount,
                        trxFreezeInterval: this.takeInfo.freezeInterval,
                        txHash: tx.txID,
                        rawTx: JSON.stringify(tx),
                        currency: this.takeInfo.payCurrency,
                        payAmount: this.takeInfo.payAmount,
                        offerNo: this.resourceInfo.offerNo || '',
                        bizTime: Date.now()
                    };

                    var response = await axios.post('/resource/take', param);
                    var result = response.data;
                    console.log(result);
                    if (result && result.code == '000000') {
                        var send = await tronWeb.trx.sendRawTransaction(tx);
                        if (send && send.result) {
                            _this.tipMessage = '<@spring.message "common.pay" /><@spring.message "common.success" />';
                        } else {
                            _this.tipMessage = '<@spring.message "common.pay" /><@spring.message "common.failure" />';
                        }
                    } else {
                        _this.tipMessage = '<@spring.message "common.book" /><@spring.message "common.failure" />';
                    }
                    _this.fullscreenLoading = false;
                    _this.openTipPopup();
                } catch (e) {
                    _this.fullscreenLoading = false;
                }
            },
            computePayAmount: function() {
                return new Decimal(this.takeCustom.freezeAmount || 1)
                        .times(this.takeCustom.freezeInterval || 1)
                        .div(this.resourceInfo.trxFreezePrice || 1)
                        .times(10)
                        .ceil()
                        .div(10)
                        .toFixed(1);
            }
        },
        computed: {
            customPayAmount: function () {
                return this.computePayAmount();
            },
            customResourceAmount: function () {
                return this.computePayAmount();
            }
        },
        created: function () {
            this.loadData();
        }
    });
}
</script>