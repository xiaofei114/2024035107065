import { api_Get, api_Post } from "./index.js"

//获取字典列表
export function get_getTypesPaged(data) {
    return api_Get('getTypesPaged', data)
}

//删除字典类型
export function get_deleteType(data) {
    return api_Post('deleteType', {
        typeId: data
    })
}

//禁用字典类型
export function get_banType(data) {
    return api_Get('banType', data)
}

//添加/修改字典类型
export function get_addorupdateType(data) {
    return api_Post('addorupdateType', data)
}


//获取字典项列表
export function get_getItemsPaged(data) {
    return api_Get('getItemsPaged', data)
}

//添加/修改字典项
export function get_addorupdateItem(data) {
    return api_Post('addorupdateItem', data)
}

//删除字典项
export function get_deleteItem(data) {
    return api_Post('deleteItem', {
        itemId: data
    })
}

//禁用字典项
export function get_banItem(data) {
    return api_Get('banItem', data)
}