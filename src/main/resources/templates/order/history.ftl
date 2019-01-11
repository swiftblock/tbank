<!DOCTYPE html>
<html>
<head>
    <#import "/spring.ftl" as spring />
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="chrome=1,IE=edge"/>
    <title>OrderInfo</title>
    <script type="text/javascript" src="/js/jquery.min.js"></script>
    <link rel="stylesheet" href="/css/orderHis.css" type="text/css"/>
    <script src="https://cdn.jsdelivr.net/npm/vue@2.5.21/dist/vue.js"></script>
    <script src="https://unpkg.com/axios/dist/axios.min.js"></script>
    <script src="/js/moment-with-locales.min.js"></script>


    <link rel="stylesheet" href="https://unpkg.com/element-ui/lib/theme-chalk/index.css">
    <script src="https://unpkg.com/element-ui/lib/index.js"></script>
</head>
<body class="mainbackground">
<div id="app" class="hide" :class="{show: isLoaded}" v-loading.fullscreen.lock="fullscreenLoading">
    <div class="popup-warn" v-show="showWarnPopup">
        <div class="popup">
        </div>
        <div class="prompt">
            <div class="promptPhoto">
                <div class="promptWritten"><@spring.message "common.tip" /></div>
                <div class="promptLong"><br><br><br>{{ warnMessage }}
                </div>
            </div>
            <div class="operationText" @click="closeWarnPopup"><@spring.message "common.cancel" /></div>
            <div class="operation" @click="remove"><@spring.message "common.confirm" /></div>
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
        <span class="returnPrePage" @click="redirect('/')"><@spring.message "common.back" /></span>
        <div class="Title">T-Bank</div>

        <div class="orderStatus">
            <div class="statusAll" :class="{statusTab: status == 'ALL'}" @click="changeTab('ALL')">
                <@spring.message "order.status.all" />
            </div>
            <div class="statusPayment" :class="{statusTab: status == 'WAIT'}" @click="changeTab('WAIT')">
                <@spring.message "order.status.processing" />
            </div>
            <div class="statusPaid" :class="{statusTab: status == 'PAID'}" @click="changeTab('PAID')">
                <@spring.message "order.status.complete" />
            </div>
        </div>
    </div>

    <div class="recodBodyDiv" @scroll="loadMore">
        <div class="recordItem" v-for="(order, index) in orderList">
            <div class="offerInfo">
                <div class="trxOffer">
                    <div class="trxOfferText">1000TRX | 3<@spring.message "common.resource.rent.time.unit" /></div>
                    <div class="trxOfferStatus">{{ order.status }}</div>
                </div>
                <div class="trxPrice">{{ order.payAmount }}{{ order.currency }}</div>
                <div class="energy">{{ order.resourceAmount }} Energy</div>
            </div>
            <div class="offerOther">
                <@spring.message "common.wallet.addr" />：<span class="offerOtherAddress">{{ order.userAddress }}</span>
            </div>
            <div class="offerOtherDate">
                {{ order.gmtCreate }}
            </div>
            <div class="operationButton">
                <div class="delButton" @click="openWarnPopup(index)"><@spring.message "common.delete" /></div>
                <div class="payButton" v-show="order.status == 'WAIT'" @click="pay(index)"><@spring.message "common.topay" /></div>
            </div>
        </div>
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
                console.error('Error: ' + name + ' is not connected');
            }
            return connected;
        }).includes(false);
        if (connected){
            // 3. 如果一切正常，启动应用。
            init();
        } else {
            console.error(`Error: TRON node is not connected`);
            console.error('wait for tronLink');
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
            warnMessage: '',
            tipMessage: '',
            showWarnPopup: false,
            showTipPopup: false,
            hasMoreData: true,
            selectedIndex: 0,
            orderList: [],
            count: 5,
            startOrderNo: '',
            status: 'ALL'
        },
        methods: {
            redirect: function (url) {
                location.href = url;
            },
            changeTab: function (status) {
                if (this.status != status) {
                    this.orderList.splice(0);
                    this.startOrderNo = '';
                    this.status = status;
                    $('.recodBodyDiv').scrollTop(0);

                    this.loadData();
                }
            },
            loadMore: function () {
                if (!this.hasMoreData) {
                    return;
                }

                var ele = $('.recodBodyDiv');
                if (ele.scrollTop() + ele.height() >= ele[0].scrollHeight) {
                    this.loadData();
                }
            },
            loadData: function () {
                this.fullscreenLoading = true;
                axios.post('/order/list', {
                    userAddress: tronWeb.defaultAddress.base58,
                    startOrderNo: this.startOrderNo,
                    count: this.count,
                    status: this.status,
                    sign: ""
                }).then(function (response) {
                    vm.fullscreenLoading = false;
                    var data = response.data.data;
                    if (!data || !data.orderList) {
                        return;
                    }
                    var size = data.orderList.length;
                    if (size <= 0) {
                        vm.hasMoreData = false;
                        return;
                    }
                    vm.hasMoreData = true;
                    for (var i = 0; i < size; i++) {
                        data.orderList[i].gmtCreate = moment(data.orderList[i].gmtCreate, 'x').format('YYYY-MM-DD HH:mm:ss');
                        vm.orderList.push(data.orderList[i]);
                    }
                    // vm.count = data.count;
                    vm.startOrderNo = data.startOrderNo;
                }).catch(function (error) {
                    console.log(error);
                    vm.fullscreenLoading = false;
                });
            },
            openTipPopup: function () {
                this.showTipPopup = true;
            },
            closeTipPopup: function () {
                this.showTipPopup = false;
            },
            openWarnPopup: function (index) {
                vm.selectedIndex = index;
                vm.showWarnPopup = true;
                vm.warnMessage = '<@spring.message "order.delete.warn" />';
            },
            closeWarnPopup: function () {
                vm.showWarnPopup = false;
                vm.warnMessage = '';
            },
            pay: function (index) {
                // todo 发送交易
                var _this = this;

                _this.fullscreenLoading = true;
                tronWeb.trx.sendRawTransaction(JSON.parse(this.orderList[index].rawTx))
                    .then(function(result) {
                        if (result && (result.result || result.code == 'DUP_TRANSACTION_ERROR')) {
                            _this.tipMessage = '<@spring.message "common.pay" /><@spring.message "common.success" />';
                            _this.orderList[index].status = 'PAID';
                        } else {
                            _this.tipMessage = '<@spring.message "common.pay" /><@spring.message "common.failure" />';
                        }
                        _this.openTipPopup();
                        _this.fullscreenLoading = false;
                    });
            },
            remove: function () {
                var index = vm.selectedIndex;
                vm.closeWarnPopup();
                vm.fullscreenLoading = true;
                axios.post('/order/delete', {
                    userAddress: tronWeb.defaultAddress.base58,
                    orderNo: vm.orderList[index].orderNo,
                    sign: ''
                }).then(function (response) {
                    vm.fullscreenLoading = false;
                    var result = response.data;
                    if (result && result.code == '000000') {
                        vm.orderList.splice(index, 1);
                    }
                    vm.tipMessage = result.msg;
                    vm.openTipPopup();
                }).catch(function (error) {
                    console.log(error);
                    vm.fullscreenLoading = false;
                });
            }
        },
        created: function () {
            this.loadData();
            this.isLoaded = true;
        },
        mounted: function () {
            var winHeight = 0;
            if (window.innerHeight) {
                winHeight = window.innerHeight;
            } else if (document.body && document.body.clientHeight) {
                winHeight = document.body.clientHeight;
            } else if (document.documentElement && document.documentElement.clientHeight) {
                winHeight = document.documentElement.clientHeight;
            }
            $('.recodBodyDiv').height(winHeight - 235).css({"overflow-y": "scroll;"});
        }
    });
}
</script>