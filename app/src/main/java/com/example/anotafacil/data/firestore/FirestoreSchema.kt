package com.example.anotafacil.data.firestore


internal object FirestoreSchema {
    object Collections {
        const val USERS = "users"
        const val OWNERS = "owners"
        const val OWNER_CODES = "ownerCodes"
        const val SUBSCRIPTION_ENTITLEMENTS = "subscriptionEntitlements"
        const val SUBSCRIPTION_PURCHASES = "subscriptionPurchases"
    }

    object OwnersCollections {
        const val CITIES = "cities"
        const val CUSTOMERS = "customers"
        const val PRODUCTS = "products"
        const val PURCHASES = "purchases"
    }

    object Fields {
        object Owner {
            const val NAME = "name"
            const val EMAIL = "email"
        }

        object City {
            const val NAME = "name"
        }

        object Customer {
            const val NAME = "name"
            const val CITY_ID = "cityId"
            const val LAST_PURCHASE_DATE = "lastPurchaseDate"
            const val DELETED_AT = "deletedAt"
            const val EXTRA_INFO = "extraInfo"
            const val OWES = "owes"
        }

        object Product {
            const val NAME = "name"
            const val PRICE = "price"
        }

        object Purchase {
            const val CUSTOMER_ID = "customerId"
            const val TOTAL_AMOUNT = "totalAmount"
            const val PURCHASE_DATE = "purchaseDate"
        }
    }

        object OwnerCodesFields {
        const val CODES = "codes"
        const val CREATED_AT = "createdAt"
        const val EXPIRES_AT = "expiresAt"
        const val OWNER_ID = "ownerId"
    }


    object UsersFields {
        const val OWNER_ID = "ownerId"
        const val EMAIL = "email"
        const val NAME = "name"
    }

    object SubscriptionFields {
        const val HAS_OWNER_ACCESS = "hasPremium"
        const val PRODUCT_ID = "productId"
        const val BASE_PLAN_ID = "basePlanId"
        const val PURCHASE_HASH = "purchaseHash"
        const val EXPIRES_AT = "expiresAt"
        const val SUBSCRIPTION_STATE = "subscriptionState"
        const val LATEST_SOURCE = "latestSource"
        const val UPDATED_AT = "updatedAt"
        const val UID = "uid"
    }

    object CommonUserFields {
        const val UID = "uid"
        const val EMAIL = "email"
        const val NAME = "name"
    }
}