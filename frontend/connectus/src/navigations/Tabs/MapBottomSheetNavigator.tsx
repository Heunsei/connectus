import React from 'react';
import {createMaterialTopTabNavigator} from '@react-navigation/material-top-tabs';
import {getFocusedRouteNameFromRoute} from '@react-navigation/core';
import BottomSheetAchievmentsScreen from '@/screens/map/BottomSheetAchievmentsScreen';
import BottomSheetQuickStackNavigator from '../stack/BottomSheetQuickStackNavigator';

export type MapBottomSheetTabParamList = {
  QuickMenu: undefined;
  Achievements: undefined;
  Event: undefined;
};

const Tab = createMaterialTopTabNavigator<MapBottomSheetTabParamList>();

/**
 * 바텀 시트에서 초기 접근시 상단 탭을 표시해줄 네비게이터입니다
 */
export default function MapBottomSheetNavigator() {
  return (
    <Tab.Navigator
      initialRouteName={'QuickMenu'}
      screenOptions={{
        tabBarLabelStyle: {
          fontSize: 18,
          fontFamily: 'GmarketSansTTFMedium',
        },
      }}>
      <Tab.Screen
        name="QuickMenu"
        component={BottomSheetQuickStackNavigator}
        options={({route}) => ({
          tabBarStyle: (route => {
            const routeName = getFocusedRouteNameFromRoute(route) ?? '';
            const hiddenRoutes = [
              'FeedCreate',
              'GatherCreate',
              'ChatList',
              'Feed',
              'FeedList',
              'Gather',
            ];
            if (hiddenRoutes.includes(routeName)) {
              return {display: 'none'};
            }
            return {
              display: 'flex',
            };
          })(route),
          title: '빠른메뉴',
        })}
      />
      <Tab.Screen
        name="Achievements"
        component={BottomSheetAchievmentsScreen}
        options={{
          title: '업적',
        }}
      />
    </Tab.Navigator>
  );
}
