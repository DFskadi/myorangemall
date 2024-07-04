// 查询列表接口
const getcategoryPage = (params) => {
  return $axios({
    url: '/classify/page',
    method: 'get',
    params
  })
}

// 编辑页面反查详情接口
const querycategoryById = (id) => {
  return $axios({
    url: `/classify/${id}`,
    method: 'get'
  })
}

// 删除当前列的接口
const delecategory = (id) => {
  return $axios({
    url: '/classify',
    method: 'delete',
    params: { id }
  })
}

// 修改接口
const editcategory = (params) => {
  return $axios({
    url: '/classify',
    method: 'put',
    data: { ...params }
  })
}

// 新增接口
const addcategory = (params) => {
  return $axios({
    url: '/classify',
    method: 'post',
    data: { ...params }
  })
}