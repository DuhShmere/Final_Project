package com.healthos.backend.model;

public class MealPlanData {

    // Represents a single recipe
    public static class Recipe {
        public String name, category, calories, fat, protein, carbs, ingredients, instructions;

        public Recipe(String name, String category, String calories,
                String fat, String protein, String carbs,
                String ingredients, String instructions) {
            this.name = name;
            this.category = category;
            this.calories = calories;
            this.fat = fat;
            this.protein = protein;
            this.carbs = carbs;
            this.ingredients = ingredients;
            this.instructions = instructions;
        }
    }

    // Represents one day: breakfast, lunch, dinner
    public static class DayPlan {
        public int dayNumber;
        public Recipe breakfast, lunch, dinner;

        public DayPlan(int dayNumber, Recipe breakfast, Recipe lunch, Recipe dinner) {
            this.dayNumber = dayNumber;
            this.breakfast = breakfast;
            this.lunch = lunch;
            this.dinner = dinner;
        }
    }

    // Represents a full 7-day meal plan
    public static class MealPlan {
        public String name, category, description;
        public DayPlan[] days;

        public MealPlan(String name, String category, String description, DayPlan[] days) {
            this.name = name;
            this.category = category;
            this.description = description;
            this.days = days;
        }
    }

    // =========================================================
    // ALL 6 MEAL PLANS
    // =========================================================

    public static MealPlan[] getAllPlans() {
        return new MealPlan[] {
                getWeightLossPlan1(),
                getWeightLossPlan2(),
                getMaintenancePlan1(),
                getMaintenancePlan2(),
                getMuscleGainPlan1(),
                getMuscleGainPlan2()
        };
    }

    public static MealPlan[] getPlansByCategory(String category) {
        MealPlan[] all = getAllPlans();
        int count = 0;
        for (MealPlan p : all) {
            if (p.category.equals(category))
                count++;
        }
        MealPlan[] result = new MealPlan[count];
        int i = 0;
        for (MealPlan p : all) {
            if (p.category.equals(category))
                result[i++] = p;
        }
        return result;
    }

    // =========================================================
    // WEIGHT LOSS PLAN 1 (~1400 kcal/day)
    // =========================================================
    private static MealPlan getWeightLossPlan1() {
        DayPlan[] days = new DayPlan[7];

        days[0] = new DayPlan(1,
                new Recipe("Greek Yogurt Parfait", "Breakfast", "320", "6g", "22g", "42g",
                        "1 cup non-fat Greek yogurt, 1/2 cup granola, 1/2 cup mixed berries, 1 tsp honey",
                        "Layer yogurt in a bowl. Top with granola and berries. Drizzle honey on top. Serve immediately."),
                new Recipe("Grilled Chicken Salad", "Lunch", "420", "12g", "45g", "28g",
                        "4oz grilled chicken breast, 2 cups mixed greens, 1/2 cup cherry tomatoes, 1/4 cup cucumber, 2 tbsp light vinaigrette",
                        "Grill chicken breast 6-7 min per side. Slice and place over greens. Add tomatoes and cucumber. Drizzle with vinaigrette."),
                new Recipe("Baked Salmon with Steamed Broccoli", "Dinner", "480", "18g", "48g", "22g",
                        "5oz salmon fillet, 1 cup broccoli florets, 1 tsp olive oil, lemon, garlic, salt, pepper",
                        "Preheat oven to 400F. Season salmon with garlic, lemon, salt, pepper. Bake 12-15 min. Steam broccoli 5 min. Drizzle with olive oil."));

        days[1] = new DayPlan(2,
                new Recipe("Veggie Egg White Omelette", "Breakfast", "280", "8g", "24g", "18g",
                        "4 egg whites, 1/2 cup spinach, 1/4 cup bell pepper, 1/4 cup mushrooms, 1 tsp olive oil, salt, pepper",
                        "Heat oil in pan. Saute vegetables 3 min. Pour egg whites over vegetables. Cook until set, fold and serve."),
                new Recipe("Turkey Lettuce Wraps", "Lunch", "380", "10g", "38g", "32g",
                        "4oz lean ground turkey, 4 large lettuce leaves, 1/4 cup shredded carrots, 2 tbsp low-sodium soy sauce, garlic, ginger",
                        "Cook turkey with garlic and ginger until browned. Add soy sauce and carrots. Spoon into lettuce leaves."),
                new Recipe("Zucchini Noodles with Shrimp", "Dinner", "390", "11g", "36g", "30g",
                        "2 medium zucchini, 5oz shrimp, 2 garlic cloves, 1/2 cup marinara sauce, olive oil, basil, salt, pepper",
                        "Spiralize zucchini. Saute shrimp with garlic in olive oil 3-4 min. Add marinara and zucchini noodles. Toss and serve topped with basil."));

        days[2] = new DayPlan(3,
                new Recipe("Overnight Oats", "Breakfast", "340", "7g", "14g", "54g",
                        "1/2 cup rolled oats, 1/2 cup almond milk, 1/4 cup blueberries, 1 tbsp chia seeds, 1 tsp honey",
                        "Mix oats, almond milk and chia seeds in a jar. Refrigerate overnight. Top with blueberries and honey before eating."),
                new Recipe("Lentil Soup", "Lunch", "410", "5g", "24g", "62g",
                        "1 cup red lentils, 2 cups vegetable broth, 1/2 onion, 2 garlic cloves, 1 carrot, cumin, turmeric, salt",
                        "Saute onion and garlic. Add lentils, broth, carrots and spices. Simmer 20-25 min until lentils are soft."),
                new Recipe("Chicken Stir Fry", "Dinner", "450", "12g", "42g", "36g",
                        "4oz chicken breast, 1 cup mixed vegetables, 2 tbsp low-sodium soy sauce, 1 tsp sesame oil, garlic, ginger, 1/2 cup brown rice",
                        "Cook rice. Stir fry chicken in sesame oil. Add vegetables, garlic, ginger. Add soy sauce and toss. Serve over rice."));

        days[3] = new DayPlan(4,
                new Recipe("Smoothie Bowl", "Breakfast", "310", "5g", "12g", "58g",
                        "1 banana, 1/2 cup frozen mango, 1/2 cup almond milk, 1/4 cup granola, 1 tbsp flaxseed, kiwi slices",
                        "Blend banana, mango and almond milk until thick. Pour into bowl. Top with granola, flaxseed and kiwi."),
                new Recipe("Tuna Stuffed Avocado", "Lunch", "400", "22g", "32g", "16g",
                        "1 can light tuna, 1 avocado, 2 tbsp Greek yogurt, lemon juice, celery, salt, pepper",
                        "Mix tuna with Greek yogurt, lemon juice and diced celery. Halve avocado and remove pit. Fill with tuna mixture."),
                new Recipe("Turkey Meatballs with Zucchini", "Dinner", "430", "14g", "44g", "24g",
                        "5oz lean ground turkey, 1 egg, 1/4 cup breadcrumbs, garlic, 1 zucchini, marinara sauce, Italian herbs",
                        "Mix turkey with egg, breadcrumbs, garlic and herbs. Form meatballs. Bake at 375F for 20 min. Serve with sauteed zucchini and marinara."));

        days[4] = new DayPlan(5,
                new Recipe("Cottage Cheese and Fruit", "Breakfast", "290", "4g", "26g", "36g",
                        "1 cup low-fat cottage cheese, 1/2 cup pineapple chunks, 1/4 cup strawberries, 1 tbsp honey",
                        "Scoop cottage cheese into a bowl. Top with pineapple and strawberries. Drizzle with honey. Serve chilled."),
                new Recipe("Veggie Black Bean Bowl", "Lunch", "430", "9g", "20g", "64g",
                        "1/2 cup black beans, 1/2 cup brown rice, 1/2 cup roasted peppers, 1/4 cup salsa, 1/4 avocado, lime",
                        "Cook rice. Warm black beans. Arrange rice, beans and peppers in bowl. Top with salsa, avocado and lime juice."),
                new Recipe("Baked Cod with Asparagus", "Dinner", "400", "10g", "44g", "28g",
                        "5oz cod fillet, 1 cup asparagus, 1 tsp olive oil, lemon, garlic, paprika, salt, pepper",
                        "Preheat oven to 400F. Place cod and asparagus on baking sheet. Drizzle with oil. Season with garlic, paprika, lemon. Bake 15 min."));

        days[5] = new DayPlan(6,
                new Recipe("Avocado Toast with Egg", "Breakfast", "360", "18g", "16g", "32g",
                        "2 slices whole grain bread, 1/2 avocado, 1 poached egg, red pepper flakes, lemon, salt",
                        "Toast bread. Mash avocado with lemon and salt. Spread on toast. Top with poached egg and red pepper flakes."),
                new Recipe("Chicken and Quinoa Bowl", "Lunch", "460", "11g", "46g", "42g",
                        "4oz chicken breast, 1/2 cup quinoa, 1 cup spinach, 1/4 cup cherry tomatoes, 2 tbsp lemon tahini dressing",
                        "Cook quinoa. Grill chicken and slice. Wilt spinach. Combine all in bowl. Drizzle with lemon tahini dressing."),
                new Recipe("Stuffed Bell Peppers", "Dinner", "420", "10g", "34g", "46g",
                        "2 bell peppers, 4oz lean ground beef, 1/2 cup cauliflower rice, 1/4 cup diced tomatoes, garlic, cumin, chili powder",
                        "Halve and deseed peppers. Cook beef with garlic and spices. Mix with cauliflower rice and tomatoes. Fill peppers. Bake at 375F for 25 min."));

        days[6] = new DayPlan(7,
                new Recipe("Protein Pancakes", "Breakfast", "330", "7g", "28g", "36g",
                        "1/2 cup oat flour, 1 scoop vanilla protein powder, 1 egg, 1/2 cup almond milk, 1/2 tsp baking powder, fresh berries",
                        "Mix all ingredients into batter. Cook on non-stick pan over medium heat 2-3 min per side. Serve topped with fresh berries."),
                new Recipe("Shrimp Taco Bowl", "Lunch", "420", "12g", "34g", "44g",
                        "5oz shrimp, 1/2 cup brown rice, 1/4 cup black beans, shredded cabbage, salsa, lime, cumin, chili powder",
                        "Season and saute shrimp. Cook rice. Build bowl with rice, beans, cabbage and shrimp. Top with salsa and lime juice."),
                new Recipe("Herb Roasted Chicken with Sweet Potato", "Dinner", "490", "13g", "46g", "44g",
                        "5oz chicken breast, 1 medium sweet potato, rosemary, thyme, garlic, olive oil, salt, pepper",
                        "Preheat oven to 400F. Cube sweet potato and toss with oil and herbs. Roast 20 min. Add seasoned chicken and roast 20 more min."));

        return new MealPlan("Lean Cut Plan", "Weight Loss",
                "~1400 kcal/day focused on high protein and low carb meals to support fat loss.", days);
    }

    // =========================================================
    // WEIGHT LOSS PLAN 2 (~1500 kcal/day)
    // =========================================================
    private static MealPlan getWeightLossPlan2() {
        DayPlan[] days = new DayPlan[7];

        days[0] = new DayPlan(1,
                new Recipe("Spinach Egg Muffins", "Breakfast", "280", "14g", "22g", "10g",
                        "3 eggs, 1/2 cup spinach, 1/4 cup feta cheese, 1/4 cup cherry tomatoes, salt, pepper",
                        "Preheat oven to 350F. Whisk eggs. Mix in spinach, feta and tomatoes. Pour into muffin tin. Bake 18-20 min."),
                new Recipe("Asian Chicken Lettuce Wraps", "Lunch", "400", "11g", "40g", "30g",
                        "4oz ground chicken, 4 butter lettuce leaves, water chestnuts, soy sauce, hoisin, garlic, ginger, green onions",
                        "Cook chicken with garlic and ginger. Add water chestnuts, soy sauce and hoisin. Spoon into lettuce cups. Top with green onions."),
                new Recipe("Cauliflower Fried Rice with Tofu", "Dinner", "440", "16g", "22g", "48g",
                        "2 cups cauliflower rice, 4oz firm tofu, 2 eggs, 1/2 cup peas, 2 tbsp soy sauce, sesame oil, garlic, green onions",
                        "Press and cube tofu. Stir fry with garlic in sesame oil. Add cauliflower rice, peas and soy sauce. Push aside, scramble eggs in pan, mix together."));

        days[1] = new DayPlan(2,
                new Recipe("Banana Almond Butter Toast", "Breakfast", "340", "12g", "10g", "50g",
                        "2 slices whole grain bread, 1 tbsp almond butter, 1/2 banana sliced, cinnamon",
                        "Toast bread. Spread almond butter. Top with banana slices and a sprinkle of cinnamon."),
                new Recipe("Mediterranean Tuna Salad", "Lunch", "390", "14g", "36g", "26g",
                        "1 can light tuna, 1/2 cup chickpeas, cucumber, cherry tomatoes, kalamata olives, lemon, olive oil, oregano",
                        "Drain tuna and chickpeas. Chop vegetables. Combine all in bowl. Dress with lemon juice, olive oil and oregano."),
                new Recipe("Sheet Pan Chicken and Vegetables", "Dinner", "470", "14g", "44g", "38g",
                        "5oz chicken breast, 1 cup broccoli, 1/2 cup carrots, 1/2 cup snap peas, olive oil, garlic, Italian seasoning",
                        "Preheat oven to 425F. Toss chicken and vegetables with oil and seasoning. Spread on sheet pan. Roast 25-30 min."));

        days[2] = new DayPlan(3,
                new Recipe("Chia Seed Pudding", "Breakfast", "300", "10g", "10g", "40g",
                        "3 tbsp chia seeds, 1 cup coconut milk, 1 tsp vanilla, 1 tsp honey, 1/2 cup raspberries",
                        "Mix chia seeds with coconut milk, vanilla and honey. Refrigerate overnight. Stir well and top with raspberries."),
                new Recipe("Black Bean Soup", "Lunch", "420", "6g", "22g", "66g",
                        "2 cups black beans, 2 cups vegetable broth, 1/2 onion, 2 garlic cloves, cumin, chili powder, lime, cilantro",
                        "Saute onion and garlic. Add beans, broth and spices. Simmer 15 min. Partially blend for creamy texture. Top with lime and cilantro."),
                new Recipe("Lemon Herb Tilapia with Quinoa", "Dinner", "460", "10g", "46g", "44g",
                        "5oz tilapia, 1/2 cup quinoa, 1 cup green beans, lemon zest, garlic, parsley, olive oil",
                        "Cook quinoa. Season tilapia with lemon, garlic and parsley. Pan sear 4 min per side. Steam green beans. Serve over quinoa."));

        days[3] = new DayPlan(4,
                new Recipe("Veggie Scramble", "Breakfast", "310", "16g", "20g", "16g",
                        "2 eggs, 1/2 cup mushrooms, 1/2 cup bell pepper, handful spinach, 1 tsp olive oil, salt, pepper",
                        "Heat oil in pan. Saute mushrooms and peppers 4 min. Add spinach, then pour in beaten eggs. Scramble until cooked through."),
                new Recipe("Grilled Veggie Wrap", "Lunch", "410", "12g", "18g", "54g",
                        "1 whole wheat tortilla, 1/2 cup grilled zucchini, 1/2 cup roasted red peppers, 2 tbsp hummus, spinach, feta",
                        "Spread hummus on tortilla. Layer spinach, zucchini and peppers. Crumble feta on top. Roll tightly and slice."),
                new Recipe("Beef and Broccoli", "Dinner", "460", "16g", "40g", "36g",
                        "4oz lean beef strips, 1 cup broccoli, 2 tbsp soy sauce, 1 tbsp oyster sauce, garlic, ginger, 1/2 cup brown rice",
                        "Cook rice. Stir fry beef with garlic and ginger. Add broccoli, soy sauce and oyster sauce. Toss and cook 3 more min. Serve over rice."));

        days[4] = new DayPlan(5,
                new Recipe("Acai Bowl", "Breakfast", "350", "8g", "8g", "62g",
                        "1 packet frozen acai, 1/2 banana, 1/4 cup almond milk, 1/4 cup granola, strawberries, coconut flakes",
                        "Blend acai packet with banana and almond milk until thick. Pour in bowl. Top with granola, strawberries and coconut flakes."),
                new Recipe("Salmon and Cucumber Rice Bowl", "Lunch", "450", "16g", "36g", "40g",
                        "4oz canned salmon, 1/2 cup brown rice, 1/2 cucumber, 1 tbsp rice vinegar, soy sauce, sesame seeds, avocado",
                        "Cook rice and season with rice vinegar. Top with salmon, cucumber and avocado. Drizzle soy sauce and sprinkle sesame seeds."),
                new Recipe("Turkey Vegetable Soup", "Dinner", "400", "8g", "38g", "42g",
                        "4oz turkey breast, 2 cups chicken broth, 1/2 cup diced carrots, 1/2 cup celery, 1/2 cup white beans, thyme, garlic",
                        "Saute garlic, carrots and celery. Add broth, turkey and beans. Simmer 20 min. Season with thyme, salt and pepper."));

        days[5] = new DayPlan(6,
                new Recipe("Whole Grain Waffles with Berries", "Breakfast", "360", "8g", "12g", "58g",
                        "1 cup whole wheat flour, 1 egg, 3/4 cup almond milk, 1 tbsp coconut oil, 1 tsp baking powder, mixed berries",
                        "Mix dry and wet ingredients separately, then combine. Cook in waffle iron until golden. Top with fresh berries."),
                new Recipe("Egg and Veggie Grain Bowl", "Lunch", "430", "14g", "22g", "50g",
                        "1/2 cup farro, 2 soft boiled eggs, 1 cup roasted vegetables, 2 tbsp tahini dressing, lemon, salt",
                        "Cook farro. Roast vegetables at 400F. Soft boil eggs 6 min. Assemble bowl and drizzle with tahini dressing."),
                new Recipe("Garlic Shrimp and Vegetable Pasta", "Dinner", "440", "9g", "36g", "54g",
                        "5oz shrimp, 2oz whole wheat pasta, 1 cup cherry tomatoes, garlic, olive oil, basil, lemon, parmesan",
                        "Cook pasta. Saute garlic in olive oil. Add shrimp and tomatoes, cook 4 min. Toss with pasta, lemon and basil. Finish with parmesan."));

        days[6] = new DayPlan(7,
                new Recipe("Peanut Butter Banana Oatmeal", "Breakfast", "370", "10g", "14g", "56g",
                        "1/2 cup rolled oats, 1 cup almond milk, 1 tbsp peanut butter, 1/2 banana, cinnamon",
                        "Cook oats in almond milk. Stir in peanut butter and cinnamon. Top with sliced banana."),
                new Recipe("Chicken Caesar Salad", "Lunch", "420", "16g", "44g", "22g",
                        "4oz grilled chicken, 2 cups romaine, 2 tbsp light Caesar dressing, 1 tbsp parmesan, whole grain croutons",
                        "Grill chicken and slice. Chop romaine and combine with dressing. Top with chicken, parmesan and croutons."),
                new Recipe("Baked Chicken Thigh with Roasted Veggies", "Dinner", "490", "18g", "44g", "34g",
                        "5oz chicken thigh, 1/2 cup Brussels sprouts, 1/2 cup sweet potato, olive oil, garlic, paprika, thyme",
                        "Preheat oven to 425F. Toss vegetables with oil and spices. Add seasoned chicken thigh. Roast 30-35 min until cooked through."));

        return new MealPlan("Clean Burn Plan", "Weight Loss",
                "~1500 kcal/day with balanced macros and varied meals to keep you satisfied while losing weight.",
                days);
    }

    // =========================================================
    // MAINTENANCE PLAN 1 (~2000 kcal/day)
    // =========================================================
    private static MealPlan getMaintenancePlan1() {
        DayPlan[] days = new DayPlan[7];

        days[0] = new DayPlan(1,
                new Recipe("Eggs Benedict with Whole Grain", "Breakfast", "480", "22g", "26g", "42g",
                        "2 eggs, 2 slices whole grain english muffin, 2 slices Canadian bacon, 2 tbsp hollandaise sauce, chives",
                        "Toast muffins. Pan fry Canadian bacon. Poach eggs 3 min. Assemble: muffin, bacon, egg. Drizzle hollandaise. Garnish with chives."),
                new Recipe("Quinoa Power Bowl", "Lunch", "560", "18g", "32g", "64g",
                        "3/4 cup quinoa, 3oz roasted chickpeas, 1 cup roasted vegetables, 2 tbsp tahini, lemon, paprika, garlic",
                        "Cook quinoa. Roast chickpeas at 400F with paprika 25 min. Roast vegetables. Assemble bowl and drizzle with lemon tahini."),
                new Recipe("Pesto Pasta with Chicken", "Dinner", "640", "22g", "46g", "66g",
                        "4oz chicken breast, 2oz whole wheat pasta, 2 tbsp pesto, 1/2 cup cherry tomatoes, parmesan, basil",
                        "Cook pasta. Grill and slice chicken. Toss pasta with pesto and tomatoes. Top with chicken and parmesan."));

        days[1] = new DayPlan(2,
                new Recipe("Yogurt Berry Smoothie", "Breakfast", "420", "8g", "20g", "68g",
                        "1 cup Greek yogurt, 1 cup mixed berries, 1 banana, 1/4 cup granola, 1 tbsp honey, almond milk",
                        "Blend yogurt, berries, banana and almond milk. Pour into glass. Top with granola and honey."),
                new Recipe("BLT Wrap with Sweet Potato Fries", "Lunch", "580", "20g", "28g", "70g",
                        "1 whole wheat tortilla, 3 strips turkey bacon, romaine, tomato, avocado, mayo, 1 medium sweet potato, olive oil",
                        "Bake sweet potato fries at 425F 20 min. Cook bacon. Assemble wrap with all ingredients. Serve with fries."),
                new Recipe("Salmon with Brown Rice and Green Beans", "Dinner", "620", "22g", "50g", "58g",
                        "5oz salmon, 3/4 cup brown rice, 1 cup green beans, soy sauce, garlic, sesame oil, ginger",
                        "Cook rice. Pan sear salmon in sesame oil 4 min per side. Steam green beans. Season with soy sauce and garlic."));

        days[2] = new DayPlan(3,
                new Recipe("Whole Grain Pancakes", "Breakfast", "460", "12g", "16g", "72g",
                        "1 cup whole wheat flour, 1 egg, 3/4 cup milk, 1 tbsp maple syrup, 1 tsp baking powder, fresh fruit",
                        "Mix ingredients into batter. Cook on griddle over medium heat 2-3 min per side. Serve with fresh fruit and a drizzle of maple syrup."),
                new Recipe("Chicken Noodle Soup with Bread", "Lunch", "520", "12g", "38g", "64g",
                        "4oz chicken breast, 1 cup egg noodles, 2 cups chicken broth, carrots, celery, onion, garlic, thyme, 1 slice whole grain bread",
                        "Saute vegetables. Add broth and chicken, simmer 15 min. Add noodles, cook 8 min. Season with thyme. Serve with bread."),
                new Recipe("Beef Tacos", "Dinner", "640", "26g", "40g", "62g",
                        "5oz lean ground beef, 3 corn tortillas, 1/4 cup shredded cheddar, lettuce, salsa, sour cream, taco seasoning",
                        "Brown beef with taco seasoning. Warm tortillas. Build tacos with beef, cheese, lettuce, salsa and sour cream."));

        days[3] = new DayPlan(4,
                new Recipe("Veggie Breakfast Burrito", "Breakfast", "490", "18g", "24g", "54g",
                        "1 whole wheat tortilla, 2 eggs, 1/2 cup black beans, 1/4 avocado, salsa, shredded cheese, spinach",
                        "Scramble eggs. Warm beans. Layer eggs, beans, spinach, avocado and salsa on tortilla. Sprinkle cheese and roll tightly."),
                new Recipe("Greek Salad with Pita and Hummus", "Lunch", "540", "22g", "18g", "66g",
                        "2 cups romaine, 1/2 cup feta, olives, cucumber, tomatoes, red onion, 2 tbsp Greek dressing, 1 whole wheat pita, 3 tbsp hummus",
                        "Chop and combine salad ingredients. Dress with Greek dressing. Serve with warm pita and hummus on the side."),
                new Recipe("Mushroom Risotto with Chicken", "Dinner", "660", "18g", "44g", "78g",
                        "4oz chicken breast, 3/4 cup arborio rice, 2 cups chicken broth, 1 cup mushrooms, 1/4 cup parmesan, onion, garlic, white wine",
                        "Saute onion, garlic and mushrooms. Add rice, toast 1 min. Add wine then ladle broth gradually, stirring. Finish with parmesan. Serve with sliced grilled chicken."));

        days[4] = new DayPlan(5,
                new Recipe("Avocado Egg Toast", "Breakfast", "440", "24g", "18g", "38g",
                        "2 slices whole grain bread, 1 avocado, 2 fried eggs, red pepper flakes, lemon, everything bagel seasoning",
                        "Toast bread. Mash avocado with lemon. Fry eggs to preference. Spread avocado, top with eggs. Season and serve."),
                new Recipe("Steak and Veggie Bowl", "Lunch", "580", "22g", "44g", "52g",
                        "4oz sirloin steak, 1/2 cup brown rice, 1 cup roasted vegetables, chimichurri sauce, lime",
                        "Cook rice. Season and sear steak 3-4 min per side. Rest and slice. Serve over rice with vegetables and chimichurri."),
                new Recipe("Shrimp Pasta Primavera", "Dinner", "620", "16g", "40g", "76g",
                        "5oz shrimp, 2oz whole wheat pasta, zucchini, cherry tomatoes, yellow squash, garlic, olive oil, parmesan, basil",
                        "Cook pasta. Saute garlic and vegetables in olive oil. Add shrimp, cook 3 min. Toss with pasta. Finish with parmesan and basil."));

        days[5] = new DayPlan(6,
                new Recipe("Oatmeal with Nuts and Fruit", "Breakfast", "450", "14g", "14g", "66g",
                        "3/4 cup rolled oats, 1 cup milk, 1/4 cup walnuts, 1/2 apple diced, cinnamon, honey",
                        "Cook oats in milk. Stir in cinnamon and honey. Top with walnuts and apple."),
                new Recipe("Turkey and Avocado Sandwich", "Lunch", "520", "18g", "36g", "54g",
                        "4oz turkey breast, 2 slices whole grain bread, 1/2 avocado, lettuce, tomato, mustard, red onion",
                        "Toast bread. Spread mustard and mashed avocado. Layer turkey, lettuce, tomato and onion. Serve with a side of fruit."),
                new Recipe("Chicken Tikka Masala with Rice", "Dinner", "680", "20g", "48g", "76g",
                        "5oz chicken breast, 3/4 cup basmati rice, 1/2 cup tomato sauce, 1/4 cup Greek yogurt, garlic, ginger, garam masala, cumin",
                        "Marinate chicken in yogurt and spices. Pan fry until cooked. Add tomato sauce and simmer 15 min. Serve over basmati rice."));

        days[6] = new DayPlan(7,
                new Recipe("French Toast with Berries", "Breakfast", "470", "14g", "18g", "66g",
                        "2 thick slices whole grain bread, 2 eggs, 1/4 cup milk, vanilla, cinnamon, mixed berries, maple syrup",
                        "Whisk eggs with milk, vanilla and cinnamon. Dip bread slices. Cook on buttered pan 2-3 min per side. Serve with berries and maple syrup."),
                new Recipe("Veggie and Feta Flatbread", "Lunch", "540", "20g", "20g", "66g",
                        "1 whole wheat flatbread, 3 tbsp hummus, roasted red peppers, artichokes, feta, spinach, olives, red onion",
                        "Spread hummus on flatbread. Top with all vegetables and feta. Bake at 400F for 12 min or serve as is."),
                new Recipe("BBQ Chicken with Corn and Coleslaw", "Dinner", "660", "18g", "48g", "78g",
                        "5oz chicken breast, 2 tbsp BBQ sauce, 1 ear of corn, 1 cup coleslaw mix, 2 tbsp coleslaw dressing",
                        "Grill chicken, basting with BBQ sauce. Grill corn. Toss coleslaw mix with dressing. Serve together."));

        return new MealPlan("Balanced Living Plan", "Maintenance",
                "~2000 kcal/day with a balanced macro split to maintain your current weight and fuel daily life.",
                days);
    }

    // =========================================================
    // MAINTENANCE PLAN 2 (~2100 kcal/day)
    // =========================================================
    private static MealPlan getMaintenancePlan2() {
        DayPlan[] days = new DayPlan[7];

        days[0] = new DayPlan(1,
                new Recipe("Smoked Salmon Bagel", "Breakfast", "490", "16g", "28g", "56g",
                        "1 whole grain bagel, 3oz smoked salmon, 2 tbsp cream cheese, red onion, capers, cucumber, dill",
                        "Toast bagel. Spread cream cheese on both halves. Layer smoked salmon, red onion, capers and cucumber. Garnish with dill."),
                new Recipe("Chicken and Rice Soup", "Lunch", "560", "14g", "40g", "68g",
                        "4oz chicken breast, 3/4 cup white rice, 3 cups chicken broth, carrots, celery, onion, garlic, parsley",
                        "Simmer chicken in broth with vegetables 20 min. Remove chicken, shred and return. Add rice, cook 15 min. Season with parsley."),
                new Recipe("Spaghetti Bolognese", "Dinner", "680", "22g", "42g", "80g",
                        "4oz lean ground beef, 2oz whole wheat spaghetti, 1 cup marinara, 1/4 cup parmesan, garlic, onion, Italian herbs",
                        "Brown beef with garlic and onion. Add marinara and herbs, simmer 20 min. Cook pasta. Toss with sauce. Top with parmesan."));

        days[1] = new DayPlan(2,
                new Recipe("Granola and Yogurt Bowl", "Breakfast", "460", "12g", "18g", "66g",
                        "1 cup full fat Greek yogurt, 1/2 cup granola, 1/2 cup strawberries, 1 tbsp almond butter, honey",
                        "Spoon yogurt into bowl. Top with granola. Add strawberries and drizzle with almond butter and honey."),
                new Recipe("Tuna Melt Sandwich", "Lunch", "550", "20g", "38g", "54g",
                        "1 can light tuna, 2 slices whole grain bread, 2 slices cheddar, celery, mayo, Dijon mustard, tomato",
                        "Mix tuna with mayo, celery and mustard. Spread on bread. Add tomato and cheese. Broil until cheese melts."),
                new Recipe("Chicken Stir Fry with Noodles", "Dinner", "660", "18g", "46g", "78g",
                        "5oz chicken breast, 2oz rice noodles, 2 cups mixed vegetables, 3 tbsp stir fry sauce, sesame oil, garlic, ginger",
                        "Cook noodles. Stir fry chicken in sesame oil. Add garlic, ginger and vegetables. Add sauce and noodles. Toss and serve."));

        days[2] = new DayPlan(3,
                new Recipe("Breakfast Hash", "Breakfast", "500", "20g", "24g", "52g",
                        "2 eggs, 1 medium potato diced, 1/2 bell pepper, 1/4 onion, 2 strips turkey bacon, olive oil, paprika, salt",
                        "Cook bacon. Saute potato, pepper and onion in oil with paprika until crispy. Crumble bacon in. Make wells and crack eggs in. Cover and cook 3 min."),
                new Recipe("Falafel Wrap", "Lunch", "560", "18g", "20g", "74g",
                        "4 falafel patties, 1 whole wheat pita, tzatziki, lettuce, tomato, cucumber, red onion, feta",
                        "Warm falafel. Stuff pita with falafel, vegetables and feta. Drizzle generously with tzatziki."),
                new Recipe("Baked Lemon Herb Chicken with Potatoes", "Dinner", "660", "18g", "50g", "72g",
                        "5oz chicken thigh, 2 medium potatoes, rosemary, thyme, garlic, lemon, olive oil, salt, pepper",
                        "Quarter potatoes. Toss with oil and herbs. Nestle chicken among potatoes in baking dish. Bake at 400F for 35-40 min."));

        days[3] = new DayPlan(4,
                new Recipe("Blueberry Protein Smoothie", "Breakfast", "440", "10g", "28g", "62g",
                        "1 cup blueberries, 1 scoop vanilla protein powder, 1 cup almond milk, 1/2 banana, 1 tbsp nut butter",
                        "Blend all ingredients until smooth. Pour into a tall glass and serve immediately."),
                new Recipe("BBQ Pulled Chicken Sandwich", "Lunch", "580", "14g", "44g", "70g",
                        "4oz slow cooked chicken breast, 2 tbsp BBQ sauce, 1 whole grain bun, coleslaw, pickles",
                        "Shred cooked chicken and mix with BBQ sauce. Warm and pile onto bun. Top with coleslaw and pickles."),
                new Recipe("Shrimp Fried Rice", "Dinner", "660", "16g", "38g", "84g",
                        "5oz shrimp, 1 cup cooked brown rice, 2 eggs, 1/2 cup peas, 2 tbsp soy sauce, sesame oil, garlic, green onions",
                        "Stir fry shrimp in sesame oil. Push aside. Scramble eggs. Add rice, peas, garlic and soy sauce. Toss everything together. Top with green onions."));

        days[4] = new DayPlan(5,
                new Recipe("Veggie Omelette with Toast", "Breakfast", "470", "20g", "24g", "44g",
                        "3 eggs, 1/2 cup mushrooms, 1/4 cup spinach, 1/4 cup goat cheese, 2 slices whole grain toast",
                        "Whisk eggs. Cook in buttered pan over medium. Add mushrooms and spinach. Fold with goat cheese. Serve with toast."),
                new Recipe("Poke Bowl", "Lunch", "560", "16g", "38g", "66g",
                        "4oz ahi tuna, 3/4 cup sushi rice, 1/4 avocado, edamame, cucumber, soy sauce, sesame oil, sriracha mayo",
                        "Cook sushi rice with rice vinegar. Top with diced tuna, avocado, edamame and cucumber. Drizzle with soy sauce and sriracha mayo."),
                new Recipe("Stuffed Sweet Potato", "Dinner", "640", "16g", "36g", "84g",
                        "1 large sweet potato, 1/2 cup black beans, 1/4 cup corn, 1/4 cup Greek yogurt, salsa, cheddar, cilantro",
                        "Bake sweet potato at 400F 45 min. Split open. Warm beans and corn. Fill potato with beans, corn, cheese, yogurt and salsa."));

        days[5] = new DayPlan(6,
                new Recipe("Overnight Chia Oat Parfait", "Breakfast", "460", "12g", "16g", "70g",
                        "1/2 cup rolled oats, 2 tbsp chia seeds, 1 cup almond milk, 1/2 cup Greek yogurt, 1/2 cup peach, honey",
                        "Mix oats and chia seeds with almond milk. Refrigerate overnight. Layer with yogurt and peach in a glass. Drizzle with honey."),
                new Recipe("Loaded Baked Potato Soup", "Lunch", "570", "18g", "24g", "74g",
                        "2 medium potatoes, 2 cups chicken broth, 1/4 cup sour cream, cheddar, turkey bacon, chives, garlic",
                        "Cook and cube potatoes. Simmer in broth with garlic until soft. Partially mash. Stir in sour cream. Top with cheese, bacon and chives."),
                new Recipe("Teriyaki Salmon with Rice", "Dinner", "660", "20g", "50g", "70g",
                        "5oz salmon, 3/4 cup jasmine rice, 2 tbsp teriyaki sauce, sesame seeds, green onions, broccoli",
                        "Cook rice. Coat salmon in teriyaki. Bake at 400F 15 min. Steam broccoli. Serve salmon over rice. Sprinkle sesame seeds and green onions."));

        days[6] = new DayPlan(7,
                new Recipe("Shakshuka", "Breakfast", "480", "22g", "24g", "44g",
                        "2 eggs, 1 cup crushed tomatoes, 1/2 onion, 1/2 bell pepper, garlic, cumin, paprika, feta, whole grain bread",
                        "Saute onion, pepper and garlic. Add tomatoes and spices. Simmer 10 min. Make wells and crack eggs in. Cover and cook 5-7 min. Top with feta. Serve with bread."),
                new Recipe("Chicken Caesar Wrap", "Lunch", "550", "18g", "44g", "56g",
                        "4oz grilled chicken, 1 large whole wheat tortilla, romaine, parmesan, Caesar dressing, croutons",
                        "Grill and slice chicken. Toss romaine with Caesar dressing. Fill tortilla with chicken, romaine, parmesan and croutons. Roll tightly."),
                new Recipe("Coconut Curry with Tofu and Rice", "Dinner", "660", "24g", "28g", "80g",
                        "6oz firm tofu, 3/4 cup basmati rice, 1 cup coconut milk, 1/2 cup chickpeas, curry powder, garlic, ginger, spinach",
                        "Press and cube tofu. Pan fry until golden. Saute garlic and ginger. Add coconut milk, chickpeas, curry powder and spinach. Simmer 10 min. Serve over rice."));

        return new MealPlan("Steady State Plan", "Maintenance",
                "~2100 kcal/day with diverse meals to maintain energy and support an active lifestyle.", days);
    }

    // =========================================================
    // MUSCLE GAIN PLAN 1 (~2800 kcal/day)
    // =========================================================
    private static MealPlan getMuscleGainPlan1() {
        DayPlan[] days = new DayPlan[7];

        days[0] = new DayPlan(1,
                new Recipe("Power Oatmeal", "Breakfast", "620", "16g", "36g", "84g",
                        "1 cup rolled oats, 1 cup milk, 1 scoop whey protein, 1 banana, 2 tbsp peanut butter, 1 tbsp honey",
                        "Cook oats in milk. Stir in protein powder. Top with sliced banana and peanut butter. Drizzle honey."),
                new Recipe("Chicken Rice and Broccoli", "Lunch", "760", "14g", "62g", "92g",
                        "6oz chicken breast, 1 cup white rice, 1.5 cups broccoli, soy sauce, garlic, olive oil",
                        "Cook rice. Bake chicken at 400F 20 min. Steam broccoli. Drizzle with soy sauce and garlic oil. Serve together."),
                new Recipe("Beef Burrito Bowl", "Dinner", "820", "28g", "56g", "88g",
                        "6oz lean ground beef, 1 cup brown rice, 1/2 cup black beans, 1/4 cup cheese, sour cream, guacamole, salsa",
                        "Brown beef with taco seasoning. Cook rice. Build bowl with rice, beef, beans, cheese, sour cream and guacamole."));

        days[1] = new DayPlan(2,
                new Recipe("Egg and Cheese Breakfast Sandwich", "Breakfast", "590", "24g", "32g", "62g",
                        "3 eggs, 2 slices whole grain bread, 2 slices cheddar, 2 strips turkey bacon, spinach, avocado",
                        "Cook bacon. Scramble or fry eggs. Toast bread. Layer eggs, cheese, bacon, spinach and avocado. Press together."),
                new Recipe("Tuna Pasta Salad", "Lunch", "720", "18g", "52g", "86g",
                        "2 cans light tuna, 2oz whole wheat pasta, celery, red onion, Greek yogurt, mayo, Dijon, lemon, dill",
                        "Cook and cool pasta. Drain tuna. Mix with celery, onion, yogurt, mayo and Dijon. Toss with pasta. Serve chilled."),
                new Recipe("Pork Tenderloin with Sweet Potato Mash", "Dinner", "800", "22g", "58g", "90g",
                        "6oz pork tenderloin, 2 medium sweet potatoes, butter, garlic, rosemary, thyme, olive oil",
                        "Season pork with garlic and herbs. Sear then roast at 400F 20 min. Boil and mash sweet potatoes with butter. Serve together."));

        days[2] = new DayPlan(3,
                new Recipe("Greek Yogurt and Protein Granola", "Breakfast", "580", "14g", "40g", "74g",
                        "1.5 cup full fat Greek yogurt, 3/4 cup protein granola, 1 cup mixed berries, 2 tbsp almond butter, honey",
                        "Layer yogurt in a large bowl. Top with granola, berries and almond butter. Drizzle honey."),
                new Recipe("Ground Turkey Rice Bowl", "Lunch", "740", "20g", "56g", "84g",
                        "6oz lean ground turkey, 1 cup brown rice, 1 cup roasted vegetables, teriyaki sauce, sesame seeds, green onions",
                        "Cook rice. Brown turkey. Add vegetables and teriyaki sauce. Serve over rice. Top with sesame seeds and green onions."),
                new Recipe("Salmon with Quinoa and Asparagus", "Dinner", "800", "28g", "60g", "76g",
                        "6oz salmon fillet, 3/4 cup quinoa, 1 bunch asparagus, lemon, garlic, dill, olive oil",
                        "Cook quinoa. Roast asparagus with oil and garlic at 400F 15 min. Pan sear salmon 5 min per side. Season with lemon and dill. Serve together."));

        days[3] = new DayPlan(4,
                new Recipe("Protein French Toast", "Breakfast", "610", "16g", "38g", "76g",
                        "3 thick slices brioche, 2 eggs, 1 scoop vanilla protein powder, 1/4 cup milk, cinnamon, maple syrup, berries",
                        "Whisk eggs, protein powder, milk and cinnamon. Dip bread. Cook on buttered pan 2-3 min per side. Serve with maple syrup and berries."),
                new Recipe("Chicken Caesar Pasta", "Lunch", "760", "22g", "54g", "86g",
                        "5oz grilled chicken, 2oz whole wheat penne, romaine, parmesan, Caesar dressing, croutons",
                        "Cook pasta. Grill and slice chicken. Toss pasta and romaine with Caesar dressing. Combine with chicken. Top with parmesan and croutons."),
                new Recipe("Steak with Roasted Potatoes and Green Beans", "Dinner", "860", "30g", "58g", "88g",
                        "7oz sirloin steak, 2 medium potatoes, 1 cup green beans, butter, garlic, rosemary, olive oil",
                        "Cube and roast potatoes with rosemary at 425F 25 min. Steam green beans. Season steak with garlic and salt. Sear 4 min per side. Rest 5 min. Serve with butter."));

        days[4] = new DayPlan(5,
                new Recipe("Mass Gainer Smoothie", "Breakfast", "640", "18g", "40g", "86g",
                        "2 scoops whey protein, 1 cup whole milk, 1 banana, 2 tbsp peanut butter, 1 tbsp oats, 1 tbsp honey",
                        "Blend all ingredients until smooth. Drink immediately after blending for best taste."),
                new Recipe("Pulled Chicken and Sweet Potato Bowl", "Lunch", "740", "16g", "58g", "88g",
                        "6oz chicken breast, 1 large sweet potato, 1/2 cup black beans, BBQ sauce, sour cream, cheddar",
                        "Slow cook or boil chicken and shred. Bake sweet potato. Build bowl with all ingredients. Top with BBQ sauce and sour cream."),
                new Recipe("Lamb Chops with Couscous", "Dinner", "820", "34g", "52g", "78g",
                        "6oz lamb chops, 3/4 cup couscous, 1 cup roasted vegetables, garlic, rosemary, mint, olive oil, lemon",
                        "Season lamb with garlic and rosemary. Cook couscous with boiling water 5 min. Sear lamb chops 3-4 min per side. Serve with couscous, vegetables and mint."));

        days[5] = new DayPlan(6,
                new Recipe("Cottage Cheese Pancakes", "Breakfast", "580", "18g", "36g", "70g",
                        "1 cup cottage cheese, 2 eggs, 1/2 cup oat flour, 1 tsp vanilla, berries, maple syrup",
                        "Blend cottage cheese and eggs. Mix in oat flour and vanilla. Cook on non-stick pan over medium heat. Serve with berries and maple syrup."),
                new Recipe("Beef and Vegetable Stir Fry with Rice", "Lunch", "760", "22g", "54g", "86g",
                        "6oz lean beef strips, 1 cup white rice, 2 cups mixed vegetables, oyster sauce, soy sauce, sesame oil, garlic, ginger",
                        "Cook rice. Marinate beef in soy sauce. Stir fry beef in sesame oil. Add vegetables, garlic, ginger and oyster sauce. Serve over rice."),
                new Recipe("Whole Roasted Chicken Leg with Rice Pilaf", "Dinner", "860", "30g", "60g", "86g",
                        "8oz chicken leg quarter, 1 cup rice pilaf, 1 cup roasted carrots, herbs de Provence, olive oil, garlic, lemon",
                        "Season chicken with herbs and garlic. Roast at 400F 40-45 min. Prepare rice pilaf. Roast carrots alongside chicken last 20 min."));

        days[6] = new DayPlan(7,
                new Recipe("Big Breakfast Scramble", "Breakfast", "620", "28g", "38g", "54g",
                        "4 eggs, 3oz turkey sausage, 1/2 cup bell peppers, 1/4 cup onion, 1/2 cup cheddar, 2 slices whole grain toast",
                        "Cook sausage. Saute peppers and onion. Scramble eggs with sausage and vegetables. Top with cheddar. Serve with toast."),
                new Recipe("Chicken Gyros with Tzatziki", "Lunch", "720", "20g", "52g", "82g",
                        "5oz chicken breast, 2 whole wheat pitas, tzatziki, tomato, cucumber, red onion, feta, lemon, oregano",
                        "Marinate chicken in lemon and oregano. Grill and slice. Fill pitas with chicken, vegetables and feta. Drizzle with tzatziki."),
                new Recipe("Seared Tuna Steak with Edamame Rice", "Dinner", "800", "26g", "64g", "80g",
                        "7oz tuna steak, 1 cup jasmine rice, 1/2 cup edamame, soy sauce, sesame oil, ginger, wasabi, green onions",
                        "Cook rice with edamame. Season tuna with soy sauce and ginger. Sear in sesame oil 90 seconds per side for medium rare. Serve over rice with wasabi and green onions."));

        return new MealPlan("Mass Builder Plan", "Muscle Gain",
                "~2800 kcal/day with high protein and carb-rich meals designed to fuel muscle growth.", days);
    }

    // =========================================================
    // MUSCLE GAIN PLAN 2 (~2900 kcal/day)
    // =========================================================
    private static MealPlan getMuscleGainPlan2() {
        DayPlan[] days = new DayPlan[7];

        days[0] = new DayPlan(1,
                new Recipe("Steak and Eggs", "Breakfast", "660", "30g", "52g", "42g",
                        "5oz sirloin steak, 3 eggs, 2 slices whole grain toast, butter, garlic, salt, pepper",
                        "Season steak with garlic, salt and pepper. Sear in butter 3-4 min per side. Rest 5 min. Fry eggs to preference. Serve with toast."),
                new Recipe("Protein Mac and Cheese with Chicken", "Lunch", "800", "22g", "64g", "90g",
                        "5oz chicken breast, 2oz whole wheat macaroni, 1/4 cup cheddar, 2 tbsp cream cheese, milk, garlic powder, mustard powder",
                        "Cook pasta. Make cheese sauce with milk, cheddar, cream cheese and seasonings. Grill and slice chicken. Toss pasta with sauce. Top with chicken."),
                new Recipe("Slow Cooker Beef Stew", "Dinner", "840", "26g", "58g", "92g",
                        "6oz beef chuck, 2 potatoes cubed, 2 carrots, 1 cup beef broth, tomato paste, onion, garlic, thyme, rosemary",
                        "Brown beef in pan. Add all ingredients to slow cooker. Cook on low 6-8 hours or high 3-4 hours. Season to taste."));

        days[1] = new DayPlan(2,
                new Recipe("Banana Protein Pancakes", "Breakfast", "630", "14g", "42g", "80g",
                        "2 bananas, 3 eggs, 1 scoop protein powder, 1/2 cup oat flour, baking powder, cinnamon, maple syrup",
                        "Mash bananas. Mix with eggs, protein powder, oat flour and baking powder. Cook on griddle. Serve with maple syrup."),
                new Recipe("Salmon Rice Bowl", "Lunch", "780", "24g", "56g", "86g",
                        "6oz baked salmon, 1 cup white rice, 1/2 avocado, edamame, cucumber, ponzu sauce, sesame seeds",
                        "Cook rice. Bake salmon at 400F 15 min. Build bowl with rice, salmon, avocado, edamame and cucumber. Drizzle ponzu and sesame seeds."),
                new Recipe("Chicken Alfredo", "Dinner", "860", "28g", "60g", "94g",
                        "6oz chicken breast, 2oz fettuccine, 3 tbsp butter, 1/2 cup heavy cream, 1/2 cup parmesan, garlic, parsley",
                        "Cook pasta. Season and pan fry chicken. Make Alfredo by melting butter with garlic, adding cream and parmesan until thickened. Toss with pasta. Slice chicken on top."));

        days[2] = new DayPlan(3,
                new Recipe("High Protein Overnight Oats", "Breakfast", "640", "16g", "44g", "80g",
                        "3/4 cup rolled oats, 1 cup full fat milk, 1 scoop protein powder, 2 tbsp almond butter, banana, chia seeds, honey",
                        "Mix oats, milk, protein powder and chia seeds. Refrigerate overnight. Top with sliced banana and almond butter. Drizzle honey."),
                new Recipe("Turkey Meatball Sub", "Lunch", "780", "22g", "54g", "88g",
                        "6oz ground turkey meatballs, 1 whole grain sub roll, 1/2 cup marinara, mozzarella, basil, garlic butter",
                        "Make and bake meatballs at 375F 20 min. Toast roll with garlic butter. Fill with meatballs and marinara. Top with mozzarella and broil until melted."),
                new Recipe("Grilled Chicken with Pasta and Pesto", "Dinner", "860", "26g", "62g", "94g",
                        "6oz chicken breast, 2.5oz whole wheat pasta, 3 tbsp pesto, cherry tomatoes, pine nuts, parmesan, basil",
                        "Cook pasta. Grill chicken. Toss pasta with pesto and tomatoes. Slice chicken and serve on top. Garnish with pine nuts, parmesan and basil."));

        days[3] = new DayPlan(4,
                new Recipe("Smashed Avocado Eggs on Toast", "Breakfast", "610", "24g", "30g", "62g",
                        "3 eggs, 2 slices sourdough, 1 avocado, feta, sun dried tomatoes, red pepper flakes, lemon",
                        "Toast sourdough. Mash avocado with lemon. Fry eggs. Spread avocado on toast. Top with eggs, feta, sun dried tomatoes and pepper flakes."),
                new Recipe("Beef Bulgogi Rice Bowl", "Lunch", "800", "22g", "56g", "92g",
                        "6oz lean beef, 1 cup white rice, bok choy, soy sauce, sesame oil, ginger, garlic, brown sugar, green onions",
                        "Marinate beef in soy sauce, sesame oil, ginger, garlic and brown sugar. Stir fry until caramelized. Steam bok choy. Serve over rice with green onions."),
                new Recipe("BBQ Ribs with Corn and Potato Salad", "Dinner", "880", "34g", "52g", "90g",
                        "7oz pork ribs, 3 tbsp BBQ sauce, 1 ear corn, 1 cup potato salad, garlic, paprika, salt",
                        "Season ribs with dry rub. Bake at 300F 2.5 hours covered. Uncover, coat with BBQ sauce, bake 30 more min. Serve with corn and potato salad."));

        days[4] = new DayPlan(5,
                new Recipe("Protein Waffles", "Breakfast", "650", "18g", "44g", "76g",
                        "1 cup whole wheat flour, 1 scoop vanilla protein, 2 eggs, 3/4 cup milk, 1 tbsp coconut oil, baking powder, berries, maple syrup",
                        "Mix dry ingredients. Whisk wet ingredients separately. Combine. Cook in waffle iron until golden. Top with berries and maple syrup."),
                new Recipe("Grilled Chicken Burrito", "Lunch", "790", "22g", "58g", "88g",
                        "5oz grilled chicken, 1 large whole wheat tortilla, 1/2 cup brown rice, black beans, cheddar, guacamole, sour cream, salsa",
                        "Grill and slice chicken. Warm rice and beans. Fill tortilla with all ingredients. Fold tightly into burrito. Grill seam side down to seal."),
                new Recipe("Pan Seared Duck Breast with Wild Rice", "Dinner", "860", "36g", "52g", "80g",
                        "6oz duck breast, 3/4 cup wild rice blend, 1 cup green beans, orange glaze, garlic, thyme, butter",
                        "Score duck skin. Season with garlic and thyme. Sear skin side down 8 min. Flip and cook 5 min. Make glaze with orange juice and butter. Serve over wild rice with green beans."));

        days[5] = new DayPlan(6,
                new Recipe("Loaded Breakfast Bowl", "Breakfast", "670", "26g", "40g", "66g",
                        "3 eggs, 1/2 cup ground turkey sausage, 1 cup hash browns, 1/2 cup peppers and onions, cheddar, hot sauce",
                        "Cook sausage. Pan fry hash browns until crispy. Saute peppers and onions. Scramble eggs. Combine in bowl. Top with cheese and hot sauce."),
                new Recipe("Chicken Shawarma Rice Bowl", "Lunch", "800", "22g", "58g", "88g",
                        "5oz chicken thigh, 1 cup basmati rice, hummus, tzatziki, tomato, cucumber, red onion, pita chips, shawarma spices",
                        "Marinate chicken in shawarma spices. Grill or pan cook until done. Cook rice. Build bowl with rice, sliced chicken, hummus, tzatziki and vegetables."),
                new Recipe("Honey Garlic Salmon with Fried Rice", "Dinner", "860", "24g", "60g", "92g",
                        "6oz salmon, 1 cup cooked white rice, 2 eggs, peas, carrots, soy sauce, honey, garlic, sesame oil, green onions",
                        "Mix honey, garlic and soy sauce. Pan sear salmon, basting with glaze 4 min per side. Stir fry rice with eggs, peas and carrots in sesame oil. Serve together."));

        days[6] = new DayPlan(7,
                new Recipe("Chorizo Breakfast Scramble", "Breakfast", "660", "30g", "38g", "56g",
                        "3oz chorizo, 3 eggs, 1/2 cup potatoes, 1/4 cup onion, jalapeno, cheddar, salsa, 2 slices toast",
                        "Cook chorizo. Add diced potatoes, onion and jalapeno. Cook until tender. Add beaten eggs and scramble. Top with cheddar and salsa. Serve with toast."),
                new Recipe("Protein Loaded Nachos", "Lunch", "810", "28g", "52g", "82g",
                        "4oz lean ground beef, tortilla chips, 1/2 cup black beans, shredded cheese, jalapenos, sour cream, guacamole, salsa",
                        "Brown beef with taco seasoning. Layer chips on baking sheet. Top with beef, beans and cheese. Bake at 400F 10 min. Top with cold toppings."),
                new Recipe("Surf and Turf", "Dinner", "880", "34g", "66g", "72g",
                        "4oz sirloin steak, 4oz jumbo shrimp, 1 cup mashed potatoes, asparagus, butter, garlic, lemon, parsley",
                        "Make mashed potatoes. Season steak and sear 4 min per side. Rest. Saute shrimp in garlic butter 3 min. Steam asparagus. Plate with lemon and parsley."));

        return new MealPlan("Hypertrophy Plan", "Muscle Gain",
                "~2900 kcal/day with calorie-dense meals and high protein to maximize muscle hypertrophy.", days);
    }
}