<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="login-title">商城管理系统</h2>
      <el-form :model="loginForm" :rules="rules" ref="loginForm" label-width="0" class="login-form">
        <el-form-item prop="account">
          <el-input v-model="loginForm.account" prefix-icon="el-icon-user" placeholder="请输入账号" size="medium"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" prefix-icon="el-icon-lock" type="password" placeholder="请输入密码" size="medium" @keyup.enter.native="handleLogin"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" class="login-btn" @click="handleLogin">登 录</el-button>
        </el-form-item>
      </el-form>
      <div class="login-tip">默认账号：admin / 123456</div>
    </div>
  </div>
</template>

<script>
import { login } from '@/api/pms_user.js'

export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        account: '',
        password: ''
      },
      rules: {
        account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      loading: false
    }
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate((valid) => {
        if (!valid) return
        this.loading = true
        login(this.loginForm.account, this.loginForm.password).then(res => {
          this.loading = false
          if (res.result === 'success') {
            // 保存 token 和用户信息
            window.sessionStorage.setItem('token', res.token)
            window.sessionStorage.setItem('user', JSON.stringify(res.user))
            this.$message.success('登录成功')
            this.$router.push('/dashBoard')
          } else {
            this.$message.error(res.cause || '登录失败')
          }
        }).catch(() => {
          this.loading = false
        })
      })
    }
  }
}
</script>

<style scoped>
.login-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}
.login-box {
  width: 380px;
  padding: 40px 35px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.3);
}
.login-title {
  text-align: center;
  margin: 0 0 30px 0;
  color: #333;
  font-size: 22px;
  font-weight: 600;
}
.login-form {
  margin-bottom: 0;
}
.login-btn {
  width: 100%;
}
.login-tip {
  text-align: center;
  font-size: 12px;
  color: #999;
  margin-top: 10px;
}
</style>
