package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExceed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> mealList = Arrays.asList(
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,10,0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,13,0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,20,0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,10,0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,13,0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,20,0), "Ужин", 510)
        );
        List<UserMealWithExceed> res = getFilteredWithExceeded(mealList, LocalTime.of(7, 0), LocalTime.of(12,0), 2000);
        System.out.println(res);
//        .toLocalDate();
//        .toLocalTime();
    }

    public static List<UserMealWithExceed> getFilteredWithExceeded(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        class FilteredDateUserMealContainer {
            private List<UserMeal> filteredDateUserMealList;
            private int colories;

            FilteredDateUserMealContainer() {
                colories = 0;
                filteredDateUserMealList = new ArrayList<UserMeal>();
            }

            private int getColories() {
                return colories;
            }

            private List<UserMeal> getFilteredDateUserMealList() {
                return filteredDateUserMealList;
            }

            private void addMeal(UserMeal meal) {
                boolean isInTimePeriod = TimeUtil.isBetween(meal.getDateTime().toLocalTime(), startTime, endTime);
                if (isInTimePeriod) {
                    filteredDateUserMealList.add(meal);
                }
                colories = colories + meal.getCalories();
            }
        }

        Map<LocalDate, FilteredDateUserMealContainer> filteredDateMealsMap = new HashMap<LocalDate, FilteredDateUserMealContainer>();

        for (UserMeal userMeal : mealList) {
            LocalDate localDate = userMeal.getDateTime().toLocalDate();
            FilteredDateUserMealContainer filteredDateUserMealContainer = filteredDateMealsMap.getOrDefault(localDate, new FilteredDateUserMealContainer());
            filteredDateUserMealContainer.addMeal(userMeal);
            filteredDateMealsMap.putIfAbsent(localDate, filteredDateUserMealContainer);
        }

        List<UserMealWithExceed> result = new ArrayList<UserMealWithExceed>();
        for (FilteredDateUserMealContainer curContainer : filteredDateMealsMap.values()) {
            boolean exceed = false;
            if (curContainer.getColories() > caloriesPerDay) {
                exceed = true;
            }
            for (UserMeal curUserMeal : curContainer.getFilteredDateUserMealList()) {
                UserMealWithExceed userMealWithExceed = new UserMealWithExceed(curUserMeal.getDateTime(), curUserMeal.getDescription(), curUserMeal.getCalories(), exceed);
                result.add(userMealWithExceed);
            }
        }

        return result;
    }

}
