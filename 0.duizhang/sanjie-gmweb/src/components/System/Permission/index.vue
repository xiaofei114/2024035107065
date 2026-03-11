<script setup>
import { ref, reactive, onMounted } from 'vue'
import { get_PermissionList, delete_ApiMethod, ban_Method, create_ApiMethod, permission_Assignment } from '@/api/permission.js'
import { ElMessageBox, ElMessage } from 'element-plus'
import { getSysDict } from '@/utils/sys.js'
import { useUserStore } from '@/stores/user.js'

const userStore = useUserStore()
const dialogVisible = ref(userStore.permission)

const { sys_user_permission, sys_status_things, sys_interface_group } = await getSysDict('sys_user_permission', 'sys_status_things', 'sys_interface_group')

const all_total = ref(0)
const tableData = ref([])
const addMethodDialog = ref(false)
const modifyUserDialog = ref(false)
const modifyUserForm = ref({})
const queryParameters = ref({
    page: 1,
    pageSize: 10,
})
const addMethodForm = ref({})
const addMethodRules = reactive({
    name: [{
        required: true,
        message: '请输入方法名称',
        trigger: 'blur'
    }],
    group_name: [{
        required: true,
        message: '请输入所属分组',
        trigger: 'blur'
    }],
    description: [{
        required: true,
        message: '请输入方法描述',
        trigger: 'blur'
    }]
})
const ruleFormRef = ref(null)
//封禁单个玩家
const banChange = (val, id) => {
    const state = val ? 1 : 0
    ban_Method({
        methodId: id,
        banStatus: state
    })
}
//翻页方法
const pageTurning = pageNum => {
    queryParameters.value.page = pageNum
    getlist()
}
//获取玩家方法
const getlist = () => {
    get_PermissionList(queryParameters.value).then(res => {
        all_total.value = res.data.pagination.total
        tableData.value = res.data.data
    })
}

//重置按钮
const clear = () => {
    queryParameters.value.permission_level = null
    queryParameters.value.group_name = null
    queryParameters.value.is_banned = null
    getlist()
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
        delete_ApiMethod(data).then(msg => {
            if (msg.code == 200) {
                ElMessage.success("删除成功")
                getlist()
            }
        })
    })
}

const modifyPermissions = data => {
    modifyUserForm.value = {
        permission: data.permissions.map(item => item.toString()),
        id: data.id
    }
    modifyUserDialog.value = true
}

const sure_modifyPermissions = () => {
    permission_Assignment({
        methodId: modifyUserForm.value.id,
        permissions: modifyUserForm.value.permission
    }).then(data => {
        if (data.code == 200) {
            ElMessage.success("修改成功")
            getlist()
        }
        modifyUserDialog.value = false
    })
}

const addMethod = () => addMethodDialog.value = true

const createMethod = async formEl => {
    if (!formEl) return
    await formEl.validate((valid) => {
        if (valid) {
            create_ApiMethod(addMethodForm.value).then(data => {
                if (data.code == 200) {
                    ElMessage.success("创建成功")
                    getlist()
                }
                addMethodDialog.value = false
            })
            addMethodForm.value = {}
        }
    })
}
</script>

<template>
    <div>
        <div>
            <el-form :model="queryParameters" label-width="auto" style="width: 100%;margin: 20px 0;" inline>
                <el-row :gutter="10">
                    <el-col :span="5">
                        <el-form-item label="接口归属" style="width: 100%;">
                            <el-select v-model="queryParameters.permission_level" placeholder="请选择接口归属" clearable
                                multiple no-data-text="无数据" no-match-text="无数据">
                                <el-option v-for="(item, index) in sys_user_permission" :key="index"
                                    :label="item.dict_label" :value="item.dict_key" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="5">
                        <el-form-item label="接口分组" style="width: 100%;">
                            <el-select v-model="queryParameters.group_name" placeholder="请选择分组名称" clearable
                                no-data-text="无数据" no-match-text="无数据">
                                <el-option v-for="(item, index) in sys_interface_group" :key="index"
                                    :label="item.dict_label" :value="item.dict_key" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="5">
                        <el-form-item label="接口状态" style="width: 100%;">
                            <el-select v-model="queryParameters.is_banned" placeholder="请选择接口状态" clearable
                                no-data-text="无数据" no-match-text="无数据">
                                <el-option v-for="(item, index) in sys_status_things" :key="index"
                                    :label="item.dict_label" :value="item.dict_key" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <div style="width: 100%; display: flex; justify-content: center;">
                                <el-button type="primary" @click="getlist" style="width: 45%;" plain>
                                    <el-icon>
                                        <Search />
                                    </el-icon>
                                    <span>查询</span>
                                </el-button>
                                <el-button type="info" @click="clear" style="width: 45%;" plain>
                                    <el-icon>
                                        <Refresh />
                                    </el-icon>
                                    <span>重置</span>
                                </el-button>
                            </div>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
        </div>
        <!-- 筛选还没做 -->
        <div class="operate">
            <el-button class="operate_button" type="primary" @click="addMethod" plain
                v-show="dialogVisible.createApiMethod !== false">创建权限</el-button>
        </div>
        <el-table :data="tableData" style="width: 100%"
            :header-cell-style="{ 'text-align': 'center', 'color': '#000' }">
            <el-table-column align="center" type="index" :index="(queryParameters.page - 1) * queryParameters.pageSize + 1" label="排序" width="60" />
            <el-table-column align="center" prop="name" label="接口名称" />
            <el-table-column align="center" prop="description" label="接口描述" />
            <el-table-column align="center" prop="group_name" label="接口分组">
                <template #default=data>
                    <dict-tag :value="data.row.group_name" :data="sys_interface_group" />
                </template>
            </el-table-column>
            <el-table-column align="center" prop="permissions" label="接口归属" width="300">
                <template #default=data>
                    <dict-tag v-for="(item, index) in data.row.permissions" :key="index" :value="item"
                        :data="sys_user_permission" />
                    <span v-if="!data.row.permissions.length">-</span>
                </template>
            </el-table-column>
            <el-table-column align="center" prop="is_banned" label="全员允许" v-if="dialogVisible.banMethod !== false">
                <template #default=data>
                    <el-switch :disabled="data.row.username == 'root'" @change="banChange($event, data.row.id)"
                        v-model="data.row.is_banned" />
                </template>
            </el-table-column>
            <el-table-column align="center" label="操作" width="300"
                v-if="dialogVisible.permissionAssignment !== false || dialogVisible.deleteApiMethod !== false">
                <template #default=data>
                    <el-button type="primary" text class="button" @click="modifyPermissions(data.row)"
                        :disabled="data.row.username == 'root'" v-show="dialogVisible.permissionAssignment !== false">
                        <el-icon>
                            <DocumentCopy />
                        </el-icon>
                        分配权限
                    </el-button>
                    <el-button type="danger" text class="button" @click="deletePlayers(data.row.id)"
                        :disabled="data.row.username == 'root'" v-show="dialogVisible.deleteApiMethod !== false">
                        <el-icon>
                            <Delete />
                        </el-icon>
                        删除权限
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
            <el-form-item label="有权角色">
                <el-select v-model="modifyUserForm.permission" placeholder="请分配角色" clearable multiple no-data-text="无数据"
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
        <el-dialog v-model="addMethodDialog" title="创建权限" width="500" append-to-body>
            <el-form ref="ruleFormRef" :model="addMethodForm" label-width="auto" style="max-width: 600px"
                :rules="addMethodRules">
                <el-form-item label="接口名称" prop="name">
                    <el-input v-model="addMethodForm.name" placeholder="请输入接口名称" />
                </el-form-item>
                <el-form-item label="接口描述" prop="description">
                    <el-input v-model="addMethodForm.description" placeholder="请输入接口描述" />
                </el-form-item>
                <el-form-item label="所属分组" prop="group_name">
                    <el-select v-model="addMethodForm.group_name" placeholder="请选择所属分组" clearable no-data-text="无数据"
                        no-match-text="无数据">
                        <el-option v-for="(item, index) in sys_interface_group" :key="index" :label="item.dict_label"
                            :value="item.dict_key" />
                    </el-select>
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="addMethodDialog = false" plain>关闭</el-button>
                    <el-button type="primary" @click="createMethod(ruleFormRef)" plain>确定</el-button>
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
    .operate > .operate_button, .operate > button { flex: 1 1 31%; min-width: 110px; height: 36px; }

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