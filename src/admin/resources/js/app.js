/**
 * First we will load all of this project's JavaScript dependencies which
 * includes Vue and other libraries. It is a great starting point when
 * building robust, powerful web applications using Vue and Laravel.
 */

require('./bootstrap');

window.Vue = require('vue');

/**
 * The following block of code may be used to automatically register your
 * Vue components. It will recursively scan this directory for the Vue
 * components and automatically register them with their "basename".
 *
 * Eg. ./components/ExampleComponent.vue -> <example-component></example-component>
 */

// const files = require.context('./', true, /\.vue$/i)
// files.keys().map(key => Vue.component(key.split('/').pop().split('.')[0], files(key).default))

Vue.component('example-component', require('./components/ExampleComponent.vue').default);
Vue.component('chart-boxes', require('./components/Widgets/ChartBoxes3.vue').default);
Vue.component('analythics', require('./components/Dashboards/Analytics.vue').default);

//Charts
Vue.component('Chartjs', require('./components/Charts/Chartjs.vue').default);

//Components
Vue.component('Accordions', require('./components/Components/Accordions.vue').default);
Vue.component('carousel', require('./components/Components/Carousel.vue').default);
Vue.component('maps', require('./components/Components/Maps.vue').default);
Vue.component('modals', require('./components/Components/Modals.vue').default);
Vue.component('pagination', require('./components/Components/Pagination.vue').default);
Vue.component('probress-bar', require('./components/Components/ProgressBar.vue').default);
Vue.component('tabs', require('./components/Components/Tabs.vue').default);
Vue.component('tooltips-popovers', require('./components/Components/TooltipsPopovers.vue').default);

//Layouts - Components -Header
Vue.component('demo-card', require('./components/Layout/Components/DemoCard.vue').default);
Vue.component('custom-footer', require('./components/Layout/Components/Footer.vue').default);
// Vue.component('header', require('./components/Layout/Components/Header.vue').default);
Vue.component('layout-wrapper', require('./components/Layout/Components/LayoutWrapper.vue').default);
Vue.component('page-title', require('./components/Layout/Components/PageTitle.vue').default);
Vue.component('page-title-sub', require('./components/Layout/Components/PageTitle2.vue').default);
Vue.component('custom-sidebar', require('./components/Layout/Components/Sidebar.vue').default);
//Layouts - Components - Header
// Vue.component('search-box', require('./components/Layout/Components/Header/SearchBox.vue').default);
// Vue.component('header-user-area', require('./components/Layout/Components/Header/HeaderUserArea.vue').default);


//
//Layouts - Wrappers

/**
 * Next, we will create a fresh Vue application instance and attach it to
 * the page. Then, you may begin adding components to this application
 * or customize the JavaScript scaffolding to fit your unique needs.
 */

const app = new Vue({
    el: '#app',
});
