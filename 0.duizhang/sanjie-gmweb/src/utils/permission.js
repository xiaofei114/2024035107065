import { get_PermissionList } from "@/api/permission.js";

/**
 * 返回权限列表
 * 该函数用于获取当前用户有权限访问的所有权限项
 * @returns {Promise<Object>} 返回一个对象，键为权限名称，值为布尔值，表示是否拥有该权限
 */
export async function returnPermissionList(userPermission) {
    const havePermission = await get_PermissionList({
        page: 1,
        pageSize: 1000,
        permission_level: userPermission,
        is_banned: 0
    }).then(res => res.data.data);
    const allPermission = await get_PermissionList({
        page: 1,
        pageSize: 1000,
        is_banned: 0
    }).then(res => res.data.data);
    return Object.fromEntries(
        allPermission.map(item => [item.name, havePermission.some(permission => permission.name === item.name)])
    );
}