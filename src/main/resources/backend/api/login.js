function loginApi(data) {
  return $axios({
    'url': '/staff/login',
    'method': 'post',
    data
  })
}

function logoutApi(){
  return $axios({
    'url': '/staff/logout',
    'method': 'post',
  })
}
