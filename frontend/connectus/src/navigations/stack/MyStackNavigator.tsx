import React from 'react';
import {createStackNavigator} from '@react-navigation/stack';
import colors from '@/constants/colors';
import MyHomeScreen from '@/screens/my/MyHomeScreen';
import MyAchievementsScreen from '@/screens/my/MyAchievementsScreen';
import MyWalkHistoryScreen from '@/screens/my/MyWalkHistoryScreen';
import MyWalkDetailScreen from '@/screens/my/MyWalkDetailScreen';
import {fonts} from '@/constants';
import MyChangeAvatarScreen from '@/screens/my/MyChangeAvatarScreen';

export type MyStackParamList = {
  MyHome: undefined;
  MyAchievements: undefined;
  MyWalkHistory: undefined;
  MyWalkDetail: {walkId: number};
  MyChangeAvatar: {currentAvatar: string};
};

const Stack = createStackNavigator<MyStackParamList>();

function MyStackNavigator() {
  return (
    <Stack.Navigator
      initialRouteName="MyHome"
      screenOptions={{
        headerStyle: {
          backgroundColor: colors.background,
          borderColor: colors.dividerColor,
        },
        headerTitleStyle: {fontFamily: fonts.medium},
      }}>
      <Stack.Screen
        name="MyHome"
        component={MyHomeScreen}
        options={{
          title: '마이페이지',
          headerShown: false,
        }}
      />
      <Stack.Screen
        name="MyAchievements"
        component={MyAchievementsScreen}
        options={{title: '업적'}}
      />
      <Stack.Screen
        name="MyWalkHistory"
        component={MyWalkHistoryScreen}
        options={{title: '산책 기록'}}
      />
      <Stack.Screen
        name="MyWalkDetail"
        component={MyWalkDetailScreen}
        options={{title: '기록 상세'}}
      />
      <Stack.Screen
        name="MyChangeAvatar"
        component={MyChangeAvatarScreen}
        options={{title: '아바타 변경'}}
      />
    </Stack.Navigator>
  );
}

export default MyStackNavigator;
