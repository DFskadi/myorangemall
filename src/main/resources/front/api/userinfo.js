
function  getuserinfoApi(userPhone){
    return $axios({
        'url': '/addressBook/userPhone',
        'method': 'get',
        userPhone
    })
}

function queryEmployeeById (userPhone) {
    return $axios({
        url: `/employee/${userPhone}`,
        method: 'get'

    })
}

function  updateUserinfoApi(data){
    return $axios({
        'url': '/addressBook/userPhone',
        'method': 'get',
        data
    })
}