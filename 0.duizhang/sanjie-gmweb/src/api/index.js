
import HttpClient from '@/utils/HttpClient.js'

const Client = new HttpClient()

//接口方法
export function api_Get(url, data) {
    return Client.get(`/api/get/${url}`, data)
}

export function api_Post(url, data) {
    return Client.post(`/api/post/${url}`, data)
}

export function api_Login(data) {
    return Client.post(`/login`, data)
}