# KartGarage
카트 차고 파싱
### 사용 예시

    garageParser = new GarageParser(this, "인디벨");
    garageParser.setRiderDataReceiveListener(new GarageParser.RiderDataReceiveListener() {
        @Override
        public void onRiderDataReceive(GarageParser.RiderData riderData) {
           textView.append("\nName : " + riderData.getRiderName() + "\nGuild Name " + riderData.getGuildName());
        }
    });
    garageParser.setItemDataReceiveListener(new GarageParser.ItemDataReceiveListener() {
        @Override
        public void onItemCountReceive(int characterCount, int kartCount, int wearingCount, int embellishmentCount, int etcCount) {
            textView.append("\nItem Count");
            textView.append("\nCharacter Count : " + characterCount + "\nKart Count : " + kartCount + "\nWearing Count : " + wearingCount + "\nEmbel Count : " + embellishmentCount + "\nEtc Count : " + etcCount);
        }

        @Override
        public void onRepresentationItemReceive(ArrayList<GarageParser.ItemData> items) {
            textView.append("\nRepresentation");
            for (GarageParser.ItemData item : items)
                textView.append("\nItem Name : " + item.getName() + "\n\tItem Count : " + item.getCount());
        }

        @Override
        public void onAllItemReceive(GarageParser.ItemData item) {
            textView.append("\nItem Name : " + item.getName() + "\n\tItem Count : " + item.getCount());
        }
    });
    garageParser.parseMain();
    garageParser.parseItem();
	
### 주의하세요.
* 파싱을 그만 둘 때는 stopTask() 메소드를 꼭 호출해 주세요.
* 이미지를 가져오는데, 길드마크가 없거나, 홈페이지 오류때문에 null값이 반환 될 수 있습니다. NullPointerException이 일어나지 않도록 조심하세요.

### 참고하세요.
* 모든 리스너는 순차적으로 실행됩니다.
* 따라서 권장되는 파싱 순서는 **RiderData** > Record > Replay > Emblem > **Item** 입니다.   
* ItemDataReceiveListener의 onAllItemReceive 콜백은 아이템 하나하나 파싱될 때 마다 호출됩니다.   
대상의 아이템이 많다고 걱정하실 필요 없고 받아올 때 마다 추가해 주시면 됩니다.

