import { api_Get, api_Post } from "./index.js"

//获取用户数据
export function get_userlist(data) {
    return api_Get('listAllUsers', data)
}

//封禁解封管理员账号
export function api_setBanStatus(data) {
    return api_Get('setBanStatus', data)
}

//重置密码
export function api_resetPassword(data) {
    return api_Post('resetPassword', data)
}

//修改权限
export function api_updatePermission(data) {
    return api_Post('updatePermission', data)
}

//删除用户
export function user_deleteUser(data) {
    return api_Post('deleteUser', { data })
}

//创建用户
export function api_addUser(data) {
    return api_Post('addUser', data)
}