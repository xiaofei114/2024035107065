import { api_Get } from "./index.js"

export function get_QueryLogs(data) {
    return api_Get("getQueryLogs", data)
}