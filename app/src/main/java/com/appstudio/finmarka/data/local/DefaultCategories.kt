package com.appstudio.finmarka.data.local

import com.appstudio.finmarka.data.local.entity.CategoryEntity
import com.appstudio.finmarka.data.model.TransactionType

object DefaultCategories {

    private val incomeCategories = listOf(
        "💼" to "Salary / Wages",
        "🧑‍💻" to "Freelance / Consulting",
        "🏢" to "Business Income",
        "📈" to "Investments (Dividends / Interest)",
        "🏠" to "Rental Income",
        "💵" to "Bonuses",
        "🎁" to "Gifts Received",
        "🧾" to "Tax Refund",
        "🏦" to "Cashback / Rewards",
        "🔄" to "Other Income"
    )

    private val expenseCategories = listOf(
        "🏡" to "Rent / Mortgage",
        "🧾" to "Property Tax",
        "🛠" to "Maintenance / Repairs",
        "🛋" to "Furniture",
        "🛒" to "Groceries",
        "🍔" to "Eating Out",
        "☕" to "Coffee / Snacks",
        "⛽" to "Fuel",
        "🚌" to "Public Transport",
        "🚕" to "Taxi / Ride Sharing",
        "🛠" to "Car Maintenance",
        "💳" to "Car Loan / EMI",
        "💡" to "Electricity",
        "🚰" to "Water",
        "📶" to "Internet",
        "📱" to "Mobile Bill",
        "📺" to "TV / OTT Subscription",
        "🩺" to "Doctor Visits",
        "💊" to "Medicines",
        "🧪" to "Lab Tests",
        "🛡" to "Health Insurance",
        "📚" to "School / College Fees",
        "🖊" to "Courses / Certifications",
        "🧑‍🏫" to "Coaching / Training",
        "👕" to "Clothing",
        "👟" to "Shoes",
        "🧴" to "Personal Care",
        "🎁" to "Gifts Given",
        "🎥" to "Movies",
        "🎮" to "Games",
        "🎵" to "Music Subscription",
        "📺" to "Streaming Services",
        "✈️" to "Flights",
        "🏨" to "Hotels",
        "🧳" to "Vacation Expenses",
        "💳" to "Credit Card Payment",
        "🏦" to "Loan EMI",
        "📉" to "Investments",
        "💰" to "Savings",
        "🐾" to "Pet Care",
        "🙏" to "Donations / Charity",
        "📌" to "Miscellaneous"
    )

    fun getDefaultCategories(): List<CategoryEntity> {
        val list = mutableListOf<CategoryEntity>()

        expenseCategories.forEach { (icon, name) ->
            list.add(
                CategoryEntity(
                    name = name,
                    type = TransactionType.EXPENSE.name,
                    icon = icon,
                    color = "#E57373",
                    isSystem = true
                )
            )
        }

        incomeCategories.forEach { (icon, name) ->
            list.add(
                CategoryEntity(
                    name = name,
                    type = TransactionType.INCOME.name,
                    icon = icon,
                    color = "#66BB6A",
                    isSystem = true
                )
            )
        }

        return list
    }
}
