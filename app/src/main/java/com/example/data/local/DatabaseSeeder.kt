package com.example.data.local

import com.example.data.model.*

object DatabaseSeeder {

    fun getDefaultCategories(): List<CategoryEntity> {
        return listOf(
            CategoryEntity(1, "Atta & Flour", "ic_flour", "Fresh wheat flour, besan, sooji, maida", 1),
            CategoryEntity(2, "Rice", "ic_rice", "Basmati, Kolam, Sona Masoori, brown rice", 2),
            CategoryEntity(3, "Dal & Pulses", "ic_pulses", "Toor dal, moong, chana, rajma, urad", 3),
            CategoryEntity(4, "Oil & Ghee", "ic_oil", "Mustard oil, sunflower oil, pure desi ghee", 4),
            CategoryEntity(5, "Masala & Spices", "ic_spices", "Haldi, mirchi, dhaniya, garam masala, whole spices", 5),
            CategoryEntity(6, "Salt & Sugar", "ic_sugar", "Iodized salt, rock salt, refined sugar, jaggery", 6),
            CategoryEntity(7, "Dry Fruits & Nuts", "ic_nuts", "Almonds, cashews, raisins, walnuts, pista", 7),
            CategoryEntity(8, "Biscuits", "ic_biscuits", "Glucose, marie, cream cookies, digestive", 8),
            CategoryEntity(9, "Namkeen & Snacks", "ic_snacks", "Bhujia, mixture, chips, roasted namkeen", 9),
            CategoryEntity(10, "Tea & Coffee", "ic_tea", "Patti tea, green tea, instant filter coffee", 10),
            CategoryEntity(11, "Breakfast & Cereals", "ic_cereal", "Corn flakes, oats, muesli, chocos", 11),
            CategoryEntity(12, "Dairy & Milk", "ic_dairy", "Toned milk, full cream, paneer, butter, curd", 12),
            CategoryEntity(13, "Bread & Bakery", "ic_bread", "Brown bread, pav, buns, rusk, cakes", 13),
            CategoryEntity(14, "Beverages", "ic_beverages", "Cold drinks, juices, energy drinks, soda", 14),
            CategoryEntity(15, "Instant Food", "ic_noodles", "Maggi noodles, pasta, ready to eat, soup", 15),
            CategoryEntity(16, "Sauces & Spreads", "ic_sauce", "Tomato ketchup, mayonnaise, jam, peanut butter", 16),
            CategoryEntity(17, "Personal Care", "ic_personal", "Soaps, shampoo, toothpaste, handwash", 17),
            CategoryEntity(18, "Household Cleaning", "ic_cleaning", "Detergent powder, dishwash gel, floor cleaner", 18),
            CategoryEntity(19, "Baby Care", "ic_baby", "Diapers, baby wipes, baby shampoo, baby food", 19),
            CategoryEntity(20, "Pooja Essentials", "ic_pooja", "Agarbatti, dhoop, kapoor, matchsticks, diya batti", 20),
            CategoryEntity(21, "Other Grocery", "ic_other", "Kitchen foils, matchboxes, tissue papers", 21)
        )
    }

    fun getDefaultProducts(): List<ProductEntity> {
        return listOf(
            ProductEntity(
                id = 1,
                sku = "BBS-ATT-001",
                name = "Aashirvaad Shudh Chakki Atta",
                brand = "Aashirvaad",
                category = "Atta & Flour",
                subcategory = "Whole Wheat Atta",
                description = "100% pure whole wheat flour processed from high-quality grains, rich in dietary fiber for softer and nutritious rotis.",
                weight = "10 kg",
                unit = "bag",
                sellingPrice = 419.0,
                mrp = 490.0,
                discountPercentage = 14,
                stockQuantity = 45,
                lowStockThreshold = 10,
                mainImage = "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=500&auto=format&fit=crop&q=80",
                additionalImages = "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=500&auto=format&fit=crop&q=80",
                isActive = true,
                isFeatured = true,
                isBestseller = true
            ),
            ProductEntity(
                id = 2,
                sku = "BBS-OIL-002",
                name = "Fortune Sunlite Refined Sunflower Oil",
                brand = "Fortune",
                category = "Oil & Ghee",
                subcategory = "Sunflower Oil",
                description = "Light and healthy refined sunflower oil enriched with Vitamin A and Vitamin D for everyday delicious cooking.",
                weight = "1 L",
                unit = "pouch",
                sellingPrice = 138.0,
                mrp = 175.0,
                discountPercentage = 21,
                stockQuantity = 60,
                lowStockThreshold = 15,
                mainImage = "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=500&auto=format&fit=crop&q=80",
                additionalImages = "",
                isActive = true,
                isFeatured = true,
                isBestseller = true
            ),
            ProductEntity(
                id = 3,
                sku = "BBS-RIC-003",
                name = "India Gate Basmati Rice Feast Rozana",
                brand = "India Gate",
                category = "Rice",
                subcategory = "Basmati Rice",
                description = "Aromatic medium-grain basmati rice with exquisite aroma and delicate texture, ideal for everyday family meals and pulav.",
                weight = "5 kg",
                unit = "bag",
                sellingPrice = 389.0,
                mrp = 499.0,
                discountPercentage = 22,
                stockQuantity = 30,
                lowStockThreshold = 8,
                mainImage = "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=500&auto=format&fit=crop&q=80",
                additionalImages = "",
                isActive = true,
                isFeatured = true,
                isBestseller = true
            ),
            ProductEntity(
                id = 4,
                sku = "BBS-DAL-004",
                name = "Tata Sampann Unpolished Toor Dal",
                brand = "Tata Sampann",
                category = "Dal & Pulses",
                subcategory = "Arhar / Toor Dal",
                description = "Unpolished Arhar Dal that retains its natural goodness, protein, and authentic rich taste without any chemical polish.",
                weight = "1 kg",
                unit = "pkt",
                sellingPrice = 172.0,
                mrp = 210.0,
                discountPercentage = 18,
                stockQuantity = 50,
                lowStockThreshold = 12,
                mainImage = "https://images.unsplash.com/photo-1596797038530-2c107229654b?w=500&auto=format&fit=crop&q=80",
                additionalImages = "",
                isActive = true,
                isFeatured = true,
                isBestseller = true
            ),
            ProductEntity(
                id = 5,
                sku = "BBS-DAI-005",
                name = "Amul Pasteurised Butter",
                brand = "Amul",
                category = "Dairy & Milk",
                subcategory = "Butter",
                description = "The taste of India. Delicious and creamy pasteurised table butter made from fresh milk fat.",
                weight = "500 g",
                unit = "pack",
                sellingPrice = 275.0,
                mrp = 285.0,
                discountPercentage = 4,
                stockQuantity = 25,
                lowStockThreshold = 6,
                mainImage = "https://images.unsplash.com/photo-1589985270826-4b7bb135bc9d?w=500&auto=format&fit=crop&q=80",
                additionalImages = "",
                isActive = true,
                isFeatured = false,
                isBestseller = true
            ),
            ProductEntity(
                id = 6,
                sku = "BBS-SLT-006",
                name = "Tata Salt Vacuum Evaporated Iodised Salt",
                brand = "Tata Salt",
                category = "Salt & Sugar",
                subcategory = "Table Salt",
                description = "Desh ka Namak. Pure vacuum-evaporated table salt ensuring essential daily iodine intake for optimal health.",
                weight = "1 kg",
                unit = "pkt",
                sellingPrice = 27.0,
                mrp = 30.0,
                discountPercentage = 10,
                stockQuantity = 120,
                lowStockThreshold = 20,
                mainImage = "https://images.unsplash.com/photo-1518110925495-5fe2fda0442c?w=500&auto=format&fit=crop&q=80",
                additionalImages = "",
                isActive = true,
                isFeatured = false,
                isBestseller = true
            ),
            ProductEntity(
                id = 7,
                sku = "BBS-INS-007",
                name = "Maggi 2-Minute Masala Instant Noodles",
                brand = "Nestle Maggi",
                category = "Instant Food",
                subcategory = "Noodles",
                description = "Favorite Masala taste made with the goodness of iron and a blend of 10 choicest roasted spices.",
                weight = "280 g (Pack of 4)",
                unit = "pack",
                sellingPrice = 54.0,
                mrp = 60.0,
                discountPercentage = 10,
                stockQuantity = 75,
                lowStockThreshold = 15,
                mainImage = "https://images.unsplash.com/photo-1612927601601-6638404737ce?w=500&auto=format&fit=crop&q=80",
                additionalImages = "",
                isActive = true,
                isFeatured = true,
                isBestseller = true
            ),
            ProductEntity(
                id = 8,
                sku = "BBS-TEA-008",
                name = "Brooke Bond Red Label Tea",
                brand = "Red Label",
                category = "Tea & Coffee",
                subcategory = "Black Tea",
                description = "Blended with care and taste. Red Label tea gives rich color, deep taste, and warming aroma in every cup.",
                weight = "500 g",
                unit = "box",
                sellingPrice = 245.0,
                mrp = 290.0,
                discountPercentage = 16,
                stockQuantity = 40,
                lowStockThreshold = 10,
                mainImage = "https://images.unsplash.com/photo-1544787219-7f47ccb76574?w=500&auto=format&fit=crop&q=80",
                additionalImages = "",
                isActive = true,
                isFeatured = false,
                isBestseller = true
            ),
            ProductEntity(
                id = 9,
                sku = "BBS-SNK-009",
                name = "Haldiram's Nagpur Aloo Bhujia",
                brand = "Haldiram's",
                category = "Namkeen & Snacks",
                subcategory = "Bhujia & Sev",
                description = "Crispy spicy potato sev blended with mint, red chilli, and selected Indian spices. Ideal teatime partner.",
                weight = "400 g",
                unit = "pkt",
                sellingPrice = 105.0,
                mrp = 120.0,
                discountPercentage = 13,
                stockQuantity = 50,
                lowStockThreshold = 10,
                mainImage = "https://images.unsplash.com/photo-1621996346565-e3d5d6281699?w=500&auto=format&fit=crop&q=80",
                additionalImages = "",
                isActive = true,
                isFeatured = false,
                isBestseller = true
            ),
            ProductEntity(
                id = 10,
                sku = "BBS-BIS-010",
                name = "Britannia Good Day Butter Cookies",
                brand = "Britannia",
                category = "Biscuits",
                subcategory = "Butter Cookies",
                description = "Rich buttery crunch with smile pattern that spreads happiness and cheer.",
                weight = "600 g (Pack of 5)",
                unit = "pack",
                sellingPrice = 110.0,
                mrp = 140.0,
                discountPercentage = 21,
                stockQuantity = 60,
                lowStockThreshold = 15,
                mainImage = "https://images.unsplash.com/photo-1558961363-fa8fdf82db35?w=500&auto=format&fit=crop&q=80",
                additionalImages = "",
                isActive = true,
                isFeatured = true,
                isBestseller = true
            ),
            ProductEntity(
                id = 11,
                sku = "BBS-SPC-011",
                name = "Everest Garam Masala Powder",
                brand = "Everest",
                category = "Masala & Spices",
                subcategory = "Blended Spices",
                description = "A finely ground blend of 13 authentic spices that lends rich aroma and taste to curries and dals.",
                weight = "100 g",
                unit = "box",
                sellingPrice = 78.0,
                mrp = 92.0,
                discountPercentage = 15,
                stockQuantity = 45,
                lowStockThreshold = 10,
                mainImage = "https://images.unsplash.com/photo-1596040033229-a9821ebd058d?w=500&auto=format&fit=crop&q=80",
                additionalImages = "",
                isActive = true,
                isFeatured = false,
                isBestseller = false
            ),
            ProductEntity(
                id = 12,
                sku = "BBS-CLN-012",
                name = "Surf Excel Easy Wash Detergent Powder",
                brand = "Surf Excel",
                category = "Household Cleaning",
                subcategory = "Detergents",
                description = "Removes tough stains easily while keeping clothes fragrant, bright and soft on hands.",
                weight = "3 kg",
                unit = "bag",
                sellingPrice = 379.0,
                mrp = 460.0,
                discountPercentage = 18,
                stockQuantity = 22,
                lowStockThreshold = 6,
                mainImage = "https://images.unsplash.com/photo-1585842378054-ee2e52f94ba2?w=500&auto=format&fit=crop&q=80",
                additionalImages = "",
                isActive = true,
                isFeatured = false,
                isBestseller = true
            ),
            ProductEntity(
                id = 13,
                sku = "BBS-GHE-013",
                name = "Amul Pure Desi Ghee",
                brand = "Amul",
                category = "Oil & Ghee",
                subcategory = "Desi Ghee",
                description = "Aromatic traditional desi cow ghee with granular texture, perfect for sweets, rotis, and dal tadka.",
                weight = "1 L",
                unit = "tin",
                sellingPrice = 610.0,
                mrp = 660.0,
                discountPercentage = 8,
                stockQuantity = 18,
                lowStockThreshold = 5,
                mainImage = "https://images.unsplash.com/photo-1589985270826-4b7bb135bc9d?w=500&auto=format&fit=crop&q=80",
                additionalImages = "",
                isActive = true,
                isFeatured = true,
                isBestseller = true
            ),
            ProductEntity(
                id = 14,
                sku = "BBS-DRY-014",
                name = "Nutraj California Raw Almonds (Badam)",
                brand = "Nutraj",
                category = "Dry Fruits & Nuts",
                subcategory = "Almonds",
                description = "Premium handpicked crunchy California almonds loaded with natural vitamin E, protein, and heart-healthy nutrients.",
                weight = "500 g",
                unit = "pouch",
                sellingPrice = 430.0,
                mrp = 550.0,
                discountPercentage = 22,
                stockQuantity = 15,
                lowStockThreshold = 5,
                mainImage = "https://images.unsplash.com/photo-1508061253366-f7da158b6d46?w=500&auto=format&fit=crop&q=80",
                additionalImages = "",
                isActive = true,
                isFeatured = true,
                isBestseller = false
            ),
            ProductEntity(
                id = 15,
                sku = "BBS-POO-015",
                name = "Cycle Pure Agarbathies (Lia Jasmine & Sandal)",
                brand = "Cycle Pure",
                category = "Pooja Essentials",
                subcategory = "Agarbatti",
                description = "Divine pure natural incense sticks crafted with pure essential floral extracts for soothing spiritual ambiance.",
                weight = "Pack of 2 (120 sticks)",
                unit = "pack",
                sellingPrice = 85.0,
                mrp = 100.0,
                discountPercentage = 15,
                stockQuantity = 40,
                lowStockThreshold = 10,
                mainImage = "https://images.unsplash.com/photo-1608755728617-aefab37d45f6?w=500&auto=format&fit=crop&q=80",
                additionalImages = "",
                isActive = true,
                isFeatured = false,
                isBestseller = false
            )
        )
    }

    fun getDefaultDeliverySlots(): List<DeliverySlotEntity> {
        return listOf(
            DeliverySlotEntity(1, "Morning Slot", "7:00 AM - 12:00 PM", 0.0, true),
            DeliverySlotEntity(2, "Evening Slot", "5:00 PM - 9:00 PM", 0.0, true),
            DeliverySlotEntity(3, "Express 45-Min Delivery", "Within 45 mins", 20.0, true)
        )
    }

    fun getDefaultUsers(): List<UserEntity> {
        return listOf(
            UserEntity(
                id = 1,
                name = "Store Admin",
                email = "admin@bankeybihari.com",
                phone = "9876543210",
                passwordHash = "admin123",
                role = "ADMIN"
            ),
            UserEntity(
                id = 2,
                name = "Sundeep Mishra",
                email = "sundeepmishra3330@gmail.com",
                phone = "9811223344",
                passwordHash = "customer123",
                role = "CUSTOMER"
            )
        )
    }

    fun getDefaultAddress(userId: Long): AddressEntity {
        return AddressEntity(
            id = 1,
            userId = userId,
            name = "Sundeep Mishra",
            phone = "9811223344",
            houseFlat = "Plot No. 42, Gali No. 3",
            street = "Main Market Road",
            area = "Jai Vihar, Phase 1",
            landmark = "Near Shiv Mandir & Super Store",
            city = "Najafgarh, New Delhi",
            state = "Delhi",
            pinCode = "110043",
            isDefault = true
        )
    }

    fun getSampleCsvContent(): String {
        return """
SKU,Name,Brand,Category,Subcategory,Price,MRP,Stock,Discount,Description,Weight,Unit,Image URL
BBS-ATT-002,Fortune Chakki Fresh Atta 100% Atta 0% Maida,Fortune,Atta & Flour,Wheat Atta,399,460,50,13,Fresh stone-ground wheat flour for soft rotis,10 kg,bag,https://images.unsplash.com/photo-1586201375761-83865001e31c
BBS-DAL-005,Fortune Sona Moong Dal Dhuli,Fortune,Dal & Pulses,Moong Dal,145,180,35,19,Finest unpolished yellow moong dal for easy digestion,1 kg,pkt,https://images.unsplash.com/photo-1596797038530-2c107229654b
BBS-OIL-004,Dhara Kachi Ghani Mustard Oil,Dhara,Oil & Ghee,Mustard Oil,155,190,40,18,Authentic pungent cold-pressed pure mustard oil,1 L,bottle,https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5
BBS-DAI-006,Mother Dairy Classic Dahi Curd,Mother Dairy,Dairy & Milk,Curd,40,45,30,11,Thick and creamy delicious whole milk curd,400 g,cup,https://images.unsplash.com/photo-1589985270826-4b7bb135bc9d
BBS-BEV-001,Coca-Cola Original Taste Soft Drink,Coca-Cola,Beverages,Soft Drinks,40,45,60,11,Chilled sparkling carbonated beverage,750 ml,bottle,https://images.unsplash.com/photo-1554866585-cd94860890b7
        """.trimIndent()
    }
}
