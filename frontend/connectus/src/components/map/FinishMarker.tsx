import React, {useState} from 'react';
import {Image, StyleSheet, View} from 'react-native';
import {MapMarkerProps, Marker} from 'react-native-maps';

/**
 * 경로의 끝을 알리는 Marker입니다
 *
 * @returns FinishMarker
 */
export default function FinishMarker({...props}: MapMarkerProps) {
  const [initialRender, setInitialRender] = useState<boolean>(true);

  return (
    <Marker identifier="finish" tracksViewChanges={initialRender} {...props}>
      <View style={styles.container}>
        <Image
          style={styles.image}
          source={require('@/assets/markers/ping_finish.png')}
          onLoad={() => {
            setInitialRender(false);
          }}
        />
      </View>
    </Marker>
  );
}

const styles = StyleSheet.create({
  container: {
    width: 70,
    aspectRatio: '207 / 250',
    borderRadius: 50,
    justifyContent: 'center',
    alignItems: 'center',
  },
  image: {
    width: '100%',
    height: '100%',
  },
});
