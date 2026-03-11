import { api_Get, api_Post } from "./index.js"

//获取权限列表
export function get_PermissionList(data) {
    return api_Get("getPermissionList", data)
}

//删除权限
export function delete_ApiMethod(methodId) {
    return api_Post("deleteApiMethod", { methodId })
}

//禁用权限
export function ban_Method(data) {
    return api_Get("banMethod", data)
}

//创建权限数据
export function create_ApiMethod(data) {
    return api_Post("createApiMethod", data)
}

//分配权限
export function permission_Assignment(data) {
    return api_Post("permissionAssignment", data)
}