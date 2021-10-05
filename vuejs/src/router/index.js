import Vue from 'vue'
import VueRouter from 'vue-router'
import Welcome from '@/views/Welcome'
import store from '../store'
import Management from '../views/Management'
import Users from '../views/Users'
import Forbidden from '../views/Forbidden'
import NotFound from '../views/NotFound'
import User from '../views/User'
import ConfirmAccount from '@/views/ConfirmAccount'
import Login from '../views/Login'
import Account from '@/views/Account'
import ChangePassword from '@/views/ChangePassword'
import RegisterAccount from '@/views/RegisterAccount'
import ResetPassword from "@/views/ResetPassword";
import ConfirmResetPassword from "@/views/ConfirmResetPassword"
import DeleteAccountByUser from "@/views/DeleteAccountByUser";
import RegisterEntertainer from "@/views/RegisterEntertainer";
import ChangePersonalData from "@/views/ChangePersonalData";
import ChangeEmail from "@/views/ChangeEmail";
import ConfirmChangeEmail from "@/views/ConfirmChangeEmail";
import AboutAccount from "@/views/AboutAccount";
import Offer from "@/views/Offer";
import Entertainer from "@/views/Entertainer";
import Offers from "@/views/Offers";
import EntertainerOffers from "@/views/EntertainerOffers";
import EntertainerOffer from "@/views/EntertainerOffer";
import ChangeDescription from "@/views/ChangeDescription";
import EntertainerPreview from "@/views/EntertainerPreview";
import ManagementOffers from "@/views/ManagementOffers";
import ManagementOffer from "@/views/ManagementOffer";
import OfferReservation from "../views/OfferReservation";
import EntertainerReservation from "@/views/EntertainerReservation";
import ManagementReservation from "@/views/ManagementReservation";
import Reservations from "@/views/Reservations";
import EntertainerUnavailability from "@/views/EntertainerUnavailability";
import FavoriteOffers from "@/views/FavoriteOffers";
import ClientInfo from "@/views/ClientInfo";
import ReservationEdit from "@/views/ReservationEdit";
import OfferEdit from "@/views/OfferEdit";


Vue.use(VueRouter)

const routes = [
    {
        path: '/',
        component: Welcome,
        meta: {
            authorization: {},
            breadcrumb: [],
        },
    },
    {
        path: '/reservation/:id/edit',
        component: ReservationEdit,
        meta: {
            authorization: {
                requiresAuth: true,
                allowed: ['CLIENT'],
            },
            breadcrumb: [
                {path: '/reservation', name: 'RESERVATIONS'},
            ],
        },
    },
    {
        path: '/offers',
        component: Offers,
        meta: {
            authorization: {},
            breadcrumb: [{path: '/offers', name: 'OFFERS'},],
        },
    },
    {
        path: '/client/favorite-offers',
        component: FavoriteOffers,
        meta: {
            authorization: {},
            breadcrumb: [{path: '/client/favorite-offers', name: 'FAVORITE_OFFERS'},],
        },
    },
    {
        path: '/offers/:id',
        component: Offer,
        meta: {
            authorization: {
                requiresAuth: true,
                allowed: ['CLIENT'],
            },
            breadcrumb: [{path: '/offers', name: 'OFFERS'}],
        },
    },
    {
        path: "/offers/:id/reservation",
        component: OfferReservation,
        meta: {
            authorization: {
                requiresAuth: true,
                allowed: ['CLIENT']
            },
            breadcrumb: [
                {path: '/offers', name: 'OFFERS'},
                {path: this, name: "RESERVATION"}
            ]
        }
    },
    {
        path: '/management',
        component: Management,
        meta: {
            authorization: {
                requiresAuth: true,
                allowed: ['MANAGEMENT'],
            },
            breadcrumb: [
                {path: '/management', name: 'MANAGEMENT'},
            ],
        },
    },
    {
        path: '/management/offers',
        component: ManagementOffers,
        meta: {
            authorization: {
                requiresAuth: true,
                allowed: ['MANAGEMENT'],
            },
            breadcrumb: [
                {path: '/management/offers', name: 'OFFERS'},
            ],
        },
    },
    {
        path: '/management/offers/:id',
        component: ManagementOffer,
        meta: {
            authorization: {
                requiresAuth: true,
                allowed: ['MANAGEMENT'],
            },
            breadcrumb: [
                {path: '/management/offers', name: 'OFFERS'},
            ],
        },
    },
    {
        path: '/entertainer',
        component: Entertainer,
        meta: {
            authorization: {
                requiresAuth: true,
                allowed: ['ENTERTAINER'],
            },
            breadcrumb: [
                {path: '/entertainer', name: 'ENTERTAINER'},
            ],
        },
    },
    {
        path: '/entertainerPreview/:id',
        component: EntertainerPreview,
        meta: {
            breadcrumb: [
                {path: '/entertainer', name: 'ENTERTAINER'},
            ],
        },
    },
    {
        path: '/entertainer/changeDescription',
        component: ChangeDescription,
        meta: {
            authorization: {
                requiresAuth: true,
                allowed: ['ENTERTAINER'],
            },
            breadcrumb: [
                {path: '/entertainer/ChangeDescription', name: 'CHANGE_DESCRIPTION'},
            ],
        },
    },
    {
        path: '/entertainer/offers',
        component: EntertainerOffers,
        meta: {
            authorization: {
                requiresAuth: true,
                allowed: ['ENTERTAINER'],
            },
            breadcrumb: [
                {path: '/entertainer/offers', name: 'OFFERS'},
            ],
        },
    },
    {
        path: '/entertainer/offers/:id',
        component: EntertainerOffer,
        meta: {
            authorization: {
                requiresAuth: true,
                allowed: ['ENTERTAINER'],
            },
            breadcrumb: [
                {path: '/entertainer/offers', name: 'OFFER'},
            ],
        },
    },
    {
        path: '/entertainer/offers/:id/edit',
        component: OfferEdit,
        meta: {
            authorization: {
                requiresAuth: true,
                allowed: ['ENTERTAINER'],
            },
            breadcrumb: [
                {path: '/entertainer/offers', name: 'OFFER'},
            ],
        },
    },
    {
        path: '/entertainer/reservation',
        component: EntertainerReservation,
        meta: {
            authorization: {
                requiresAuth: true,
                allowed: ['ENTERTAINER'],
            },
            breadcrumb: [
                {path: '/entertainer', name: 'ENTERTAINER'},
                {path: '/entertainer/reservation', name: 'RESERVATIONS'},
            ],
        }
    },
    {
        path: '/management/reservation',
        component: ManagementReservation,
        meta: {
            authorization: {
                requiresAuth: true,
                allowed: ['MANAGEMENT'],
            },
            breadcrumb: [
                {path: '/management', name: 'MANAGEMENT'},
                {path: '/management/reservation', name: 'RESERVATION'},
            ],
        }
    },
    {
        path: '/management/users',
        component: Users,
        meta: {
            authorization: {
                requiresAuth: true,
                allowed: ['MANAGEMENT'],
            },
            breadcrumb: [
                {path: '/management', name: 'MANAGEMENT'},
                {path: '/management/users', name: 'USERS'},
            ],
        },
    },
    {
        path: '/management/users/:id',
        component: User,
        meta: {
            authorization: {
                requiresAuth: true,
                allowed: ['MANAGEMENT'],
            },
            breadcrumb: [
                {path: '/management', name: 'MANAGEMENT'},
                {path: '/management/users', name: 'USERS'},
            ],
        },
    },
    {
        path: '/management/register_entertainer',
        component: RegisterEntertainer,
        meta: {
            authorization: {
                requiresAuth: true,
                allowed: ['MANAGEMENT'],
            },
            breadcrumb: [
                {path: '/management', name: 'MANAGEMENT'},
                {path: '/management/register_entertainer', name: 'REGISTER_ENTERTAINER_ACCOUNT'},
            ],
        },
    },
    {
        path: '/account',
        component: Account,
        meta: {
            authorization: {
                requiresAuth: true,
            },
            breadcrumb: [
                {path: '/account', name: 'MANAGE_ACCOUNT'},
            ],
        },
    },
    {
        path: '/account/change-personal-data',
        component: ChangePersonalData,
        meta: {
            authorization: {
                requiresAuth: true,
            },
            breadcrumb: [
                {path: '/account', name: 'MANAGE_ACCOUNT'},
                {path: '/account/change-data', name: 'CHANGE_PERSONAL_DATA'},
            ],
        },
    },
    {
        path: '/account/aboutAccount',
        component: AboutAccount,
        meta: {
            authorization: {
                requiresAuth: true,
            },
            breadcrumb: [
                {path: '/account', name: 'MANAGE_ACCOUNT'},
                {path: '/account/aboutAccount', name: 'ABOUT_ACCOUNT'},
            ],
        },
    },
    {
        path: '/account/change-password',
        component: ChangePassword,
        meta: {
            authorization: {
                requiresAuth: true,
            },
            breadcrumb: [
                {path: '/account', name: 'MANAGE_ACCOUNT'},
                {path: '/account/change-password', name: 'CHANGE_PASSWORD'},
            ],
        },
    },
    {
        path: '/account/change-email',
        component: ChangeEmail,
        meta: {
            authorization: {
                requiresAuth: true,
            },
            breadcrumb: [
                {path: '/account', name: 'MANAGE_ACCOUNT'},
                {path: '/account/change-email', name: 'CHANGE_EMAIL'},
            ],
        },
    },
    {
        path: '/account/change-email/confirm/:id/:token/:email',
        component: ConfirmChangeEmail,
        meta: {
            authorization: {
                requiresAuth: true,
            },
            breadcrumb: [
                {path: '/account', name: 'MANAGE_ACCOUNT'},
                {path: '/account/change-email', name: 'CHANGE_EMAIL'},
                {path: '/account/change-email/confirm', name: 'CHANGE_EMAIL_CONFIRM'},
            ],
        },
    },
    {
        path: '/account/delete-account',
        component: DeleteAccountByUser,
        meta: {
            authorization: {
                requiresAuth: true,
            },
            breadcrumb: [
                {path: '/account', name: 'MANAGE_ACCOUNT'},
                {path: '/account/delete-account', name: 'CHANGE_PASSWORD'},
            ],
        },
    },
    {
        path: '/login',
        component: Login,
        meta: {
            breadcrumb: [
                {path: '/login', name: 'LOGIN'},
            ],
        },
    },
    {
        path: '/entertainer/clientInfo/:id',
        component: ClientInfo,
        meta: {
            authorization: {
                requiresAuth: true,
                allowed: ['ENTERTAINER'],
            },
            breadcrumb: [
                {path: '/entertainer', name: 'ENTERTAINER'},
                {path: '/entertainer/reservation', name: 'RESERVATIONS'},
                {path: this, name: 'CLIENT'},
            ],
        },
    },
    {
        path: '/entertainer/offers/clientInfo/:id',
        component: ClientInfo,
        meta: {
            authorization: {
                requiresAuth: true,
                allowed: ['ENTERTAINER'],
            },
            breadcrumb: [
                {path: '/entertainer', name: 'ENTERTAINER'},
                {path: '/entertainer/offers', name: 'OFFERS'},
                {path: this, name: 'CLIENT'},
            ],
        },
    },
    {
        path: '/accountConfirmation',
        component: ConfirmAccount,
    },
    {
        path: '/resetPassword',
        component: ResetPassword,
    }, {
        path: '/confirmResetPassword',
        component: ConfirmResetPassword,
    },
    {
        path: '/register',
        component: RegisterAccount,
    },

    {
        path: '/forbidden',
        component: Forbidden,
    },
    {
        path: '*',
        component: NotFound,
    },
  {
    path: '/reservations',
    component: Reservations,
    meta: {
      authorization: {
        requiresAuth: true,
        allowed: ['CLIENT'],
      },
      breadcrumb: [
        {path: '/reservations', name: 'RESERVATIONS'},
      ],
    },
  },
  {
    path:"/entertainer/unavailability",
    component: EntertainerUnavailability,
    meta: {
      authorization: {
        requiresAuth: true,
        allowed: ['ENTERTAINER'],
      },
      breadcrumb: [
        {path: '/entertainer', name: 'ENTERTAINER'},
        {path: '/entertainer/unavailability', name: 'ENTERTAINER_UNAVAILABILITY'},
      ],
    },
  },
]

let router = new VueRouter({
    mode: 'hash',
    base: process.env.BASE_URL,
    routes,
})

let authorize = function (authRules, authenticated, accessLevel) {
    if (!authRules?.requiresAuth) return true
    if (!authenticated) return false
    if (!authRules.allowed) return true
    return authRules.allowed.includes(accessLevel)
}

router.beforeEach((to, from, next) => {
    if (to.matched.every(record => authorize(record.meta.authorization, store.getters.isAuthenticated, store.state.accessLevel))) {
        // if authorized
        if (to.path === '/') {
            // if root
            if (!store.getters.isAuthenticated || store.state.accessLevel === 'CLIENT') {
                // client and guest are allowed
                next()
            } else {
                // redirect to access level specific route
                next('/' + store.state.accessLevel.toLowerCase())
            }
        } else {
            next()
        }
    } else {
        // unauthorized
        if (!store.getters.isAuthenticated) {
            next(`/login?redirectUrl=${encodeURIComponent(to.path)}`)
        } else (
            next('/forbidden')
        )
    }
})

export default router
