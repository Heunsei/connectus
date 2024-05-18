import {
  Button,
  Dimensions,
  FlatList,
  Modal,
  Pressable,
  SafeAreaView,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import React, {useEffect, useState} from 'react';
import Carousel from 'react-native-reanimated-carousel';
import FeedPreview from '@/components/feed/FeedPreview';
import colors from '@/constants/colors';
import useModal from '@/hooks/useModal';
import {useInfiniteQuery} from '@tanstack/react-query';
import {getFeedList} from '@/api/post';
import Geolocation from '@react-native-community/geolocation';
import {LatLng} from 'react-native-maps';
import {queryKeys} from '@/constants';
import {getNearWalkRecord} from '@/api/walk';
import MainText from '@/components/text/MainText';
import queryClient from '@/api/queryClient';

export default function FeedHomeScreen() {
  const carouselWidth = Dimensions.get('window').width;
  const carouselHeight = Dimensions.get('window').height;

  // 보러가기 버튼을 눌럿을 때, update되어 이동 id를 modal에 전달해줄 state
  const [selectFeed, setSelectFeed] = useState<number | undefined>(undefined);
  // 피드 호출에 전달할 유저 위도 경도 좌표
  const [currentPos, setCurrentPos] = useState<LatLng>();
  const [isRefreshing, setIsRefreshing] = useState(false);

  // 보러가기 버튼 클릭시 실행할 update 함수 (조회수 증가함수 호출 필요)
  const handlePressViewButton = (id: number) => {
    setSelectFeed(id);
  };

  // const {data, hasNextPage, isFetchingNextPage, fetchNextPage, refetch} =
  //   useInfiniteQuery({
  //     queryFn: ({pageParam}) =>
  //       getFeedList(
  //         currentPos?.latitude as number,
  //         currentPos?.longitude as number,
  //         pageParam,
  //       ),
  //     queryKey: [queryKeys.GET_FEED_LIST],
  //     initialPageParam: 0,
  //     getNextPageParam: (lastPage, allPages) => {
  //       console.log('last page', lastPage);
  //       const lastPost = lastPage[lastPage.length - 1];
  //       return lastPost ? allPages.length + 1 : undefined;
  //     },
  //   });

  const {
    data,
    hasNextPage,
    isFetching,
    isFetchingNextPage,
    fetchNextPage,
    refetch,
  } = useInfiniteQuery({
    queryFn: ({pageParam}) => getNearWalkRecord(pageParam),
    queryKey: [queryKeys.GET_ROUTE_LIST],
    initialPageParam: 0,
    refetchInterval: 3000,
    getNextPageParam: lastPage => {
      if (lastPage.hasNext) {
        return lastPage.pageNum + 1;
      }
      return undefined;
    },
  });

  // 스크롤을 위로 당겼을 때, refetch 진행
  const handleRefresh = async () => {
    queryClient.resetQueries([queryKeys.GET_ROUTE_LIST]);
    refetch();
  };

  // scroll을 아래로 했을때 실행할 함수.
  const handleEndReached = () => {
    if (hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  };

  const {
    isVisible: isMoveModalVisible,
    show: moveModalShow,
    hide: moveModalHide,
  } = useModal();

  useEffect(() => {
    Geolocation.getCurrentPosition(
      info => {
        setCurrentPos({
          latitude: info.coords.latitude,
          longitude: info.coords.longitude,
        });
      },
      () => {
        console.log('error');
      },
      {
        // 상세 좌표를 요청하는 코드
        enableHighAccuracy: true,
        distanceFilter: 0,
        // interval: 3000,
        // fastestInterval: 2000,
      },
    );
  }, []);

  return (
    <SafeAreaView>
      <FlatList
        data={data?.pages.flatMap(page => page.walksList)}
        refreshing={isRefreshing}
        onRefresh={handleRefresh}
        onEndReached={handleEndReached}
        onEndReachedThreshold={0.5}
        scrollIndicatorInsets={{right: 1}}
        renderItem={({item}) => {
          return (
            <View style={{marginBottom: 30}}>
              <Carousel
                width={carouselWidth}
                height={carouselWidth + 80}
                mode="parallax"
                autoFillData={false}
                panGestureHandlerProps={{
                  activeOffsetX: [-10, 10],
                }}
                data={item.postList}
                renderItem={({item, index}) => {
                  // console.log('================================');
                  // console.log('item', item);
                  // console.log('================================');
                  return <FeedPreview feedId={item} />;
                }}
              />
            </View>
          );
        }}>
        {/* <Carousel
          width={carouselWidth}
          mode="parallax"
          data={test}
          renderItem={({index}) => (
            <FeedPreview
              isLiked={false}
              likeNumber={12}
              commentNumber={53}
              show={moveModalShow}
            />
          )}
        /> */}
      </FlatList>
      <Modal
        visible={isMoveModalVisible}
        transparent={true}
        animationType="slide">
        <SafeAreaView style={styles.modalBackground} onTouchEnd={moveModalHide}>
          <View style={styles.confirmModal}>
            <Text style={styles.confirmText}>
              해당 방명록의 위치를 확인하시겠습니까?
            </Text>
            <View style={styles.confirmButtonContainer}>
              <Pressable style={styles.confirimButton}>
                <Text style={styles.buttonText}>보러가기</Text>
              </Pressable>
              <Pressable style={styles.confirimButton} onPress={moveModalHide}>
                <Text style={styles.buttonText}>취소</Text>
              </Pressable>
            </View>
          </View>
        </SafeAreaView>
      </Modal>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  modalBackground: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  confirmModal: {
    borderRadius: 20,
    padding: 35,
    gap: 10,
    backgroundColor: colors.buttonBackground,
  },
  confirmText: {
    fontFamily: 'GmarketSansTTFLight',
    fontSize: 16,
    color: colors.white,
  },
  confirmButtonContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    gap: 30,
  },
  confirimButton: {
    borderRadius: 8,
    paddingHorizontal: 15,
    paddingVertical: 10,
    backgroundColor: colors.background,
  },
  buttonText: {
    fontFamily: 'GmarketSansTTFMedium',
    color: colors.white,
    fontSize: 16,
  },
});
