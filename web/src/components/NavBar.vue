<template>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
      <div class="container">
        <router-link class="navbar-brand" :to="{name: 'home'}">前端学习</router-link>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarText" aria-controls="navbarText" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarText">
          <ul class="navbar-nav me-auto mb-2 mb-lg-0">
            <li class="nav-item">
              <router-link class="nav-link" active-class="active" aria-current="page" :to="{name: 'pk_index'}">匹配对战</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" active-class="active" aria-current="page" :to="{name: 'record_index'}">对局列表</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" active-class="active" aria-current="page" :to="{name: 'ranklist_index'}">排行榜</router-link>
            </li>
          </ul>
          <ul class="navbar-nav">
            <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
            hxc
          </a>
          <ul class="dropdown-menu">
            <li><router-link class="dropdown-item" :to="{name: 'user_bot_index'}">我的bot</router-link></li>
            <li><hr class="dropdown-divider"></li>
            <li><a class="dropdown-item" href="#">退出</a></li>
          </ul>
        </li>
          </ul>
        </div>
      </div>
    </nav>
    </template>
    
    <script>
    </script>
    
    <style scoped>
    </style>