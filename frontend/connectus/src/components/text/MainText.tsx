import colors from '@/constants/colors';
import React from 'react';
import {StyleSheet, Text, TextProps} from 'react-native';

export default function MainText({children, style, ...props}: TextProps) {
  return (
    <Text style={[styles.mainText, style]} {...props}>
      {children}
    </Text>
  );
}

const styles = StyleSheet.create({
  mainText: {
    fontFamily: 'GmarketSansTTFMedium',
    fontSize: 24,
    color: colors.white,
    padding: 0,
    margin: 0,
  },
});
