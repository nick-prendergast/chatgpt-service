<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>ChatGPT Conversation</title>
    <link rel="stylesheet" type="text/css" href="/static/css/styles.css">
</head>
<body>
    <div class="container">
        <h2>ChatGPT Conversation</h2>

        <div class="chat-history">
            <c:forEach items="${conversationHistory}" var="message">
                <div class="message ${message.role == 'user' ? 'user-message' : 'assistant-message'}">
                    ${message.content}
                </div>
            </c:forEach>
        </div>

        <form action="/chatgpt" method="post" class="input-area">
            <textarea name="prompt" rows="2" placeholder="Type your message here..." required></textarea>
            <input type="submit" value="Send">
        </form>
    </div>

    <script src="/static/js/script.js"></script>
</body>
</html>