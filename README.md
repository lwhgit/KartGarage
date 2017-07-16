# KartGarage
카트 차고 파싱
### 사용 예시

    final GarageParser garageParser = new GarageParser(this, "인디벨");
    garageParser.setRiderDataReceiveListener(new GarageParser.RiderDataReceiveListener() {
        @Override
        public void onRiderDataReceive(GarageParser.RiderData riderData) {
            textView.append("Name : " + riderData.getRiderName() + "\nGuild Name " + riderData.getGuildName());
        }

        @Override
        public void onParsingEnd() {
            garageParser.parseItem();
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
            for (GarageParser.ItemData data : items)
                textView.append("\nName : " + data.getName());
        }

        @Override
        public void onAllItemReceive(GarageParser.ItemData items) {
            textView.append("\nItem Name : " + items.getName());
        }

        @Override
        public void onParsingEnd() {

        }
    });
    garageParser.parseMain();
	
### 주의하세요
모든 리스너는 순차적으로 진행되어야 합니다.

    garageParser.parseMain();
    garageParser.parseItem();
	
절대 이런식으로 사용하지 말아주세요.   
각각의 리스너는 onParsingEnd 콜백을 갖고 있습니다.   
각 콜백이 끝나는 지점에 다른 파싱작업을 시작 해 주세요.

#### 예시

    garageParser.set~~~Listener(new ~~~Listener() {
        @Override
        public void onParsingEnd() {
            garageParser.parsing~~~();
        }
    });

### 참고해 주세요.
권장되는 파싱 순서는
RiderData > Record > Replay > Emblem > Item 입니다.   
ItemDataReceiveListener의 onAllItemReceive 콜백은 아이템 하나하나 파싱될 때 마다 호출됩니다.   
대상의 아이템이 많다고 걱정하실 필요 없고 받아올 때 마다 추가해 주시면 됩니다.

