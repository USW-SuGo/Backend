function send() {
    console.log("sending");

    stompClient.send("chat/message", JSON.stringify(
        {
            roomId : 4,
            senderId : 1,
            receiverId : 2,
            message : "테스트 1",
            multipartFileList : ""
        }
    ))
}