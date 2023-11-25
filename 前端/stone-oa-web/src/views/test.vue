<template>
  <div>
    <div>账号切换</div>
    <button @click="zhangsan()" type="default" size="mini">张三</button>
    <button @click="lisi()" type="default" size="mini">李四</button>
    <button @click="admin()" type="default" size="mini">admin</button>
    <div>当前token：{{ token }}</div>
  </div>
</template>

<script>
export default {
  name: "Test",

  data() {
    return {
      token: ''
    };
  },

  created() {
    this.token = window.localStorage.getItem('token')
  },

  methods: {
    admin() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNLO0NLQwMjUz0FEqLU4t8kwBikGYeYm5qUAtiSm5mXlKtQCaaUhkQgAAAA.3MHqHpokUrgppq5EcO-ZVebryBB4XENgPrbtCCxwn-Oz0msPd-onBxYSago62LYCDbcNfZOn9pnlro62I86WNQ'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },

    zhangsan() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNLO0NLQwMjU011EqLU4t8kxRsjKGMPMSc1OBWqoyEvPSixPzlGoBFC-xHUUAAAA.HT2pXMHAbgR7VhDo4eWU3l9lhvapnK9KM2KqnxLJLIvQ6mu5Dsrs3MBhgsingktaUHFFpp-bOnTIXD27bPFW3A'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },
    lisi() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNLO0NLQwMjMz01EqLU4t8kxRsjKBMPMSc1OBWnIyizOVagEBgrYpQQAAAA.VsFqgO2EHT_2PFfIWP9dcS02XEIawu8D12q4e5gU_hEI3cnPOV00rtu7_fLroo0nfTg_NA-uvlMILaWWbb_7fw'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    }
  }
}
</script>
