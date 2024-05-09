import colors from '@/constants/colors';
import React from 'react';
import {Platform, StyleSheet, View} from 'react-native';
import MapView, {
  LatLng,
  MapViewProps,
  PROVIDER_GOOGLE,
  Polyline,
  SnapshotOptions,
} from 'react-native-maps';
import StartMarker from '@/components/map/StartMarker';
import FinishMarker from '@/components/map/FinishMarker';
import {ViewProps} from 'react-native';

/**
 * RouteMap을 생성할 시 전달할 인자를 지정합니다
 */
export interface RouteMapProps extends ViewProps {
  /**
   * 산책 경로
   */
  routes: LatLng[];
}

/**
 * 경로를 지도에 표시합니다
 *
 * @returns RouteMap
 */
export default class RouteMap extends React.Component<RouteMapProps> {
  /**
   * 지도 객체
   */
  private map: React.RefObject<MapView>;

  /**
   * 지도에 표시할 경로 목록
   */
  protected routes: LatLng[];

  /**
   * MapView에 적용할 props
   */
  protected mapProps: MapViewProps;

  /**
   * View에 적용할 props
   */
  protected viewProps: ViewProps;

  /**
   * RouteMap을 생성합니다
   *
   * @param props RouterMap 생성 시 사용할 인자
   */
  constructor(props: RouteMapProps) {
    super(props);

    this.map = React.createRef<MapView>();

    this.viewProps = props;
    this.routes = props.routes;

    this.mapProps = {
      style: styles.map,
      scrollEnabled: false,
      zoomEnabled: false,
      provider: PROVIDER_GOOGLE,
      onMapReady: this.onMapReady.bind(this),
      children: (
        <>
          <StartMarker coordinate={this.routes[0]} />
          <FinishMarker coordinate={this.routes[this.routes.length - 1]} />
          <Polyline
            coordinates={this.routes}
            strokeWidth={8}
            strokeColor={colors.dividerColor}
          />
        </>
      ),
    };
  }

  /**
   * 지도 준비 완료 시 실행할 함수입니다
   */
  protected onMapReady() {
    // 지도를 적절한 위치에 위치
    if (Platform.OS === 'ios') {
      this.map.current?.fitToElements();
    } else {
      this.map.current?.fitToCoordinates(this.routes, {
        animated: false,
        edgePadding: {
          top: 50,
          bottom: 30,
          left: 30,
          right: 30,
        },
      });
    }
  }

  /**
   * 지도의 snapshot을 이미지로 저장합니다
   *
   * @param options snapshot 설정
   * @returns 파일 uri 또는 base64 인코딩된 데이터의 Promise
   */
  takeSnapshot(options?: SnapshotOptions): ReturnType<MapView['takeSnapshot']> {
    // 기본 설정 지정
    if (typeof options === 'undefined') {
      options = {};
    }
    if (!('width' in options)) {
      options.width = 512;
    }
    if (!('height' in options)) {
      options.height = 512;
    }

    // MapView가 준비되어있을 때만 진행
    if (this.map.current === null) {
      return Promise.reject('MapView is not initialized');
    }

    return this.map.current.takeSnapshot(options);
  }

  /**
   * RouteMap을 render합니다
   *
   * @returns RouteMap
   */
  render() {
    const {style: viewStyle, ...containerProps} = this.viewProps;

    return (
      <View style={[styles.mapContainer, viewStyle]} {...containerProps}>
        <MapView {...this.mapProps} ref={this.map} />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  mapContainer: {
    aspectRatio: '1 / 1',
    borderRadius: 15,
    overflow: 'hidden',
    alignItems: 'stretch',
  },
  map: {
    flexBasis: '100%',
  },
});
