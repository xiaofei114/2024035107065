import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { returnPermissionList } from '@/utils/permission.js'
import { useRouter, useRoute } from 'vue-router'

export const useUserStore = defineStore('user', () => {
    const token = ref(localStorage.getItem("sanjie_gm_token") || null)
    const user = ref(null)
    const permission = ref(null)
    const permissionNum = ref(null)
    const router = useRouter()

    const loginIn = (newToken, newUser, userPermission) => {
        // 先保存原始 token 到 localStorage
        localStorage.setItem("sanjie_gm_token", newToken)
        // 设置带 Bearer 前缀的 token
        token.value = newToken ? (newToken.startsWith('Bearer ') ? newToken : `Bearer ${newToken}`) : ''
        user.value = newUser
        permissionNum.value = userPermission

        if (userPermission == 0) {
            permission.value = {}
        } else {
            returnPermissionList(userPermission).then(item => {
                permission.value = item
                console.log(permission.value);
            })
        }
    }

    const loginOut = () => {
        token.value = null
        localStorage.removeItem("sanjie_gm_token")
        user.value = null
        router.push('/')
    }

    //是否已登录
    const isAuthenticated = computed(() => {
        return !!token.value && !!user.value
    })

    return {
        token,
        user,
        permission,
        permissionNum,
        loginIn,
        loginOut,
        isAuthenticated,
    }
})
