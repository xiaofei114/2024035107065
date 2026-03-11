<script setup>
import { ref, reactive, onMounted } from 'vue'
import { get_userlist, api_setBanStatus, api_resetPassword, api_updatePermission, user_deleteUser, api_addUser } from "@/api/user.js"
import { ElMessageBox, ElMessage } from 'element-plus'
import { getSysDict } from '@/utils/sys.js'
import { useUserStore } from '@/stores/user.js'

const userStore = useUserStore()
const dialogVisible = ref(userStore.permission)
const { sys_user_permission } = await getSysDict('sys_user_permission')

const props = defineProps({
    userName: String,
})

const all_total = ref(0)
const tableData = ref([])
const addUserDialog = ref(false)
const modifyUserDialog = ref(false)
const modifyUserForm = ref(null)
const queryParameters = ref({
    page: 1,
    pageSize: 10,
    showSensitive: true, // 是否显示敏感字段
    filterBanned: false, // 是否过滤封禁用户
})
const addUserForm = ref({})
const addUserRules = reactive({
    account: [{
        required: true,
        message: '请输入用户名',
        trigger: 'blur'
    }],
    plainPassword: [{
        required: true,
        message: '请输入密码',
        trigger: 'blur'
    }],
    permission: [{
        required: true,
        message: '请输入权限等级',
        trigger: 'blur'
    }]
})
const ruleFormRef = ref(null)
//封禁单个玩家
const banChange = (val, id) => {
    const state = val ? 1 : 0
    api_setBanStatus({
        account: id,
        isBanned: state
    })
}
//翻页方法
const pageTurning = pageNum => {
    queryParameters.value.page = pageNum
    getlist()
}
//获取玩家方法
const getlist = () => {
    get_userlist(queryParameters.value).then(res => {
        all_total.value = res.data.pagination.total
        const permission = userStore.permissionNum
        tableData.value = res.data.data.map(item => {
            if (item.permission <= permission) {
                if (item.username === props.userName) return {
                    ...item,
                    show: true,
                    me: true
                }
                else return {
                    ...item,
                    show: true
                }
            }
            else return {
                ...item,
                show: false
            }
        })
    })
}
//数据初始化
onMounted(() => {
    getlist()
})

//删除玩家方法
const deletePlayers = data => {
    ElMessageBox.confirm('这将不可恢复!', "警告！", {
        cancelButtonText: "取消",
        confirmButtonText: "确定",
    }).then(() => {
        user_deleteUser(data).then(msg => {
            if (msg.code == 200) {
                ElMessage.success("删除成功")
                getlist()
            }
        })
    })
}

//重置密码方法
const resetPassword = data => {
    ElMessageBox.prompt('请输入新密码', "提示", {
        cancelButtonText: "取消",
        confirmButtonText: "确定",
        inputValidator: (value) => {
            if (!value) return '密码不能为空'
        }
    }).then(({ value }) => {
        api_resetPassword({
            account: data,
            newPassword: value
        }).then(res => {
            if (res.code == 200) {
                ElMessage.success("重置成功")
            }
        })
    })
}

const modifyPermissions = data => {
    modifyUserForm.value = {
        permission: data.permission.toString(),
        username: data.username
    }
    modifyUserDialog.value = true
}

const sure_modifyPermissions = () => {
    api_updatePermission({
        account: modifyUserForm.value.username,
        newPermission: modifyUserForm.value.permission
    }).then(data => {
        if (data.code == 200) {
            ElMessage.success("修改成功")
            getlist()
        }
        modifyUserDialog.value = false
    })
}

const addPlayer = () => addUserDialog.value = true

const createUser = async formEl => {
    if (!formEl) return
    await formEl.validate((valid) => {
        if (valid) {
            api_addUser(addUserForm.value).then(data => {
                if (data.code == 200) {
                    ElMessage.success("创建成功")
                    getlist()
                }
                addUserDialog.value = false
            })
            addUserForm.value = {}
        }
    })
}
</script>

<template>
    <div>
        <div class="operate">
            <div>
                <el-button class="operate_button" type="primary" @click="addPlayer" plain
                    v-show="dialogVisible.addUser !== false">创建用户</el-button>
                <el-button class="operate_button" type="warning" @click="queryParameters.showSensitive = false" plain
                    v-if="queryParameters.showSensitive">隐藏时间</el-button>
                <el-button class="operate_button" type="success" @click="queryParameters.showSensitive = true" plain
                    v-else>显示时间</el-button>
            </div>
        </div>
        <el-table :data="tableData" style="width: 100%"
            :header-cell-style="{ 'text-align': 'center', 'color': '#000' }">
            <el-table-column align="center" type="index" :index="(queryParameters.page - 1) * queryParameters.pageSize + 1" label="排序" width="60" />
            <el-table-column align="center" prop="username" label="名字" />
            <el-table-column align="center" prop="permission" label="权限">
                <template #default="data">
                    <dict-tag :value="data.row.permission" :data="sys_user_permission" />
                </template>
            </el-table-column>
            <el-table-column align="center" prop="createdAt" label="注册时间" v-if="queryParameters.showSensitive" />
            <el-table-column align="center" prop="updatedAt" label="更新时间" v-if="queryParameters.showSensitive" />
            <el-table-column align="center" prop="isBanned" label="是否封禁" v-if="dialogVisible.setBanStatus !== false">
                <template #default=data>
                    <el-switch :disabled="data.row.username == 'root' || data.row.show"
                        @change="banChange($event, data.row.username)" v-model="data.row.isBanned" />
                </template>
            </el-table-column>
            <el-table-column align="center" label="操作" width="300"
                v-if="dialogVisible.updatePermission !== false || dialogVisible.resetPassword !== false || dialogVisible.deleteUser !== false">
                <template #default=data>
                    <el-button type="primary" text class="button" @click="modifyPermissions(data.row)"
                        :disabled="data.row.username == 'root' || data.row.show"
                        v-show="dialogVisible.updatePermission !== false">
                        <el-icon>
                            <Edit />
                        </el-icon>
                        修改权限
                    </el-button>
                    <el-button type="warning" text class="button" @click="resetPassword(data.row.username)"
                        :disabled="data.row.show && !data.row.me" v-show="dialogVisible.resetPassword !== false">
                        <el-icon>
                            <Key />
                        </el-icon>
                        重置密码
                    </el-button>
                    <el-button type="danger" text class="button" @click="deletePlayers(data.row.username)"
                        :disabled="data.row.username == 'root' || data.row.show"
                        v-show="dialogVisible.deleteUser !== false">
                        <el-icon>
                            <Delete />
                        </el-icon>
                        删除用户
                    </el-button>
                </template>
            </el-table-column>
            <template #empty>
                无数据
            </template>
        </el-table>
        <div class="paging">
            <span>共 {{ all_total }} 条</span>
            <el-pagination background prev-text="上一页" next-text="下一页" layout="prev, pager, next" :total="all_total"
                :pager-count="5" @current-change="pageTurning" />
        </div>
        <el-dialog v-model="modifyUserDialog" title="修改权限" width="400" append-to-body>
            <el-form-item label="用户权限">
                <el-select v-model="modifyUserForm.permission" placeholder="请选择用户权限" no-data-text="无数据"
                    no-match-text="无数据">
                    <el-option v-for="(item, index) in sys_user_permission" :key="index" :label="item.dict_label"
                        :value="item.dict_key" />
                </el-select>
            </el-form-item>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="modifyUserDialog = false" plain>关闭</el-button>
                    <el-button type="primary" @click="sure_modifyPermissions" plain>确定</el-button>
                </div>
            </template>
        </el-dialog>
        <el-dialog v-model="addUserDialog" title="添加用户" width="500" append-to-body>
            <el-form ref="ruleFormRef" :model="addUserForm" label-width="auto" style="max-width: 600px"
                :rules="addUserRules">
                <el-form-item label="用户账号" prop="account">
                    <el-input v-model="addUserForm.account" placeholder="请输入账号" />
                </el-form-item>
                <el-form-item label="用户密码" prop="plainPassword">
                    <el-input v-model="addUserForm.plainPassword" placeholder="请输入密码" />
                </el-form-item>
                <el-form-item label="用户权限" prop="permission">
                    <el-select v-model="addUserForm.permission" placeholder="请选择用户权限">
                        <el-option v-for="(item, index) in sys_user_permission" :key="index" :label="item.dict_label"
                            :value="item.dict_key" />
                    </el-select>
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="addUserDialog = false" plain>关闭</el-button>
                    <el-button type="primary" @click="createUser(ruleFormRef)" plain>确定</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<style scoped>
.operate {
    width: 100%;
    margin-bottom: 15px;
}

.operate_button {
    width: 100px;
    height: 35px;
}

.button {
    padding: 0 3px;
}

.paging {
    margin-top: 20px;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    width: 100%;
}

.paging>span {
    font-size: 14px;
    margin-right: 18px;
    color: #484848;
}

@media screen and (max-width: 500px) {
    :deep(.el-row) { margin-right: 0 !important; margin-left: 0 !important; }
    :deep(.el-col) { flex: 0 0 100% !important; max-width: 100% !important; }

    .operate { display: flex; justify-content: center; margin: 10px 0; }
    .operate > div { width: 100%; display: flex; gap: 8px; flex-wrap: wrap; justify-content: flex-start; }
    .operate_button { flex: 1 1 31%; min-width: 110px; height: 36px; }

    :deep(.el-form) { width: 100% !important; margin: 10px 0 !important; }
    :deep(.el-form-item__label) { font-size: 14px; line-height: 1.2; }
    :deep(.el-input), :deep(.el-select), :deep(.el-input-number) { width: 100%; }

    :deep(.el-table) { font-size: 12px; }
    :deep(.el-table th), :deep(.el-table td) { padding: 8px 4px !important; }

    .paging { justify-content: center; }
    .paging > span { margin-right: 8px; font-size: 12px; }

    :deep(.el-dialog) { width: 92% !important; margin: 5vh auto !important; }
    :deep(.el-dialog__body) { max-height: 70vh; overflow: auto; }
}
</style>