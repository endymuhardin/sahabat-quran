# Gmail API Integration Setup Guide

This guide explains how to set up Gmail API integration for the Sahabat Quran application using the refresh token approach for free Gmail accounts.

## Overview

The application uses Gmail API (not SMTP) with OAuth2 refresh tokens to send emails. This approach:
- Works with free Gmail accounts
- Doesn't require runtime OAuth flows
- Automatically refreshes access tokens
- Prevents token expiration with health monitoring

## Architecture

```
┌─────────────────┐
│   Application   │
├─────────────────┤
│  EmailService   │ ◄── Interface with two implementations
├─────────────────┤
│ NoopEmailService│ ◄── Default (logs only, no actual sending)
│ GmailEmailService│ ◄── Gmail implementation (activated by profile)
└─────────────────┘
         │
         ▼
┌─────────────────┐
│   Gmail API     │
├─────────────────┤
│  Refresh Token  │ ◄── Long-lived token (6 months if unused)
│  Access Token   │ ◄── Auto-refreshed (1 hour expiry)
└─────────────────┘
```

## Prerequisites

1. Google account with Gmail
2. Google Cloud Console access
3. Java 21+ installed locally
4. Maven installed locally

## Setup Steps

### Step 1: Enable Gmail API in Google Cloud Console

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Enable Gmail API:
   - Go to "APIs & Services" → "Library"
   - Search for "Gmail API"
   - Click "Enable"

### Step 2: Create OAuth 2.0 Credentials

1. In Google Cloud Console, go to "APIs & Services" → "Credentials"
2. Click "Create Credentials" → "OAuth client ID"
3. If prompted, configure OAuth consent screen:
   - Choose "External" user type
   - Fill in required fields (app name, user support email, etc.)
   - Add your email to test users
   - Add scope: `https://www.googleapis.com/auth/gmail.send`
4. For Application type, select "Desktop app"
5. Name it (e.g., "Sahabat Quran Email Client")
6. Click "Create"
7. Download the credentials JSON file

### Step 3: Generate Refresh Token

#### Option A: Using Maven (Recommended)

```bash
# Run the token generator with Maven
./mvnw compile exec:java \
  -Dexec.mainClass="com.sahabatquran.webapp.util.GmailTokenGenerator" \
  -Dexec.args="/path/to/your/credentials.json"

# If you get class not found error, try with test-compile
./mvnw test-compile exec:java \
  -Dexec.mainClass="com.sahabatquran.webapp.util.GmailTokenGenerator" \
  -Dexec.args="/path/to/your/credentials.json"
```

#### Option B: Direct Java Compilation

```bash
# First, download dependencies
./mvnw dependency:copy-dependencies

# Compile the token generator
javac -cp "target/dependency/*" \
  src/main/java/com/sahabatquran/webapp/util/GmailTokenGenerator.java

# Run the token generator
java -cp "src/main/java:target/dependency/*" \
  com.sahabatquran.webapp.util.GmailTokenGenerator \
  /path/to/your/credentials.json
```

The utility will:
   - Open a browser for OAuth authorization
   - Ask you to login and grant permissions
   - Display the refresh token and other credentials
   - Provide ready-to-use configuration

3. Save the displayed values securely:
   - Client ID
   - Client Secret
   - Refresh Token

### Step 4: Configure Application

#### Option A: Environment Variables (Recommended for Production)

Set these environment variables:

```bash
export GMAIL_CLIENT_ID="your-client-id.apps.googleusercontent.com"
export GMAIL_CLIENT_SECRET="your-client-secret"
export GMAIL_REFRESH_TOKEN="your-refresh-token"
export GMAIL_NOTIFICATION_EMAIL="your-email@gmail.com"
```

#### Option B: Application Properties

Add to `application-gmail.properties`:

```properties
gmail.enabled=true
gmail.client-id=your-client-id
gmail.client-secret=your-client-secret
gmail.refresh-token=your-refresh-token
gmail.notification-email=your-email@gmail.com
```

**⚠️ WARNING**: Never commit credentials to version control!

### Step 5: Activate Gmail Profile

Run the application with Gmail profile:

```bash
# Using Maven
mvn spring-boot:run -Dspring.profiles.active=gmail

# Using Java
java -jar target/webapp.jar --spring.profiles.active=gmail

# Using environment variable
export SPRING_PROFILES_ACTIVE=gmail
java -jar target/webapp.jar
```

## Configuration Options

### Application Properties

```properties
# Enable/disable Gmail (default: false)
gmail.enabled=true

# OAuth2 Credentials (use environment variables in production)
gmail.client-id=${GMAIL_CLIENT_ID}
gmail.client-secret=${GMAIL_CLIENT_SECRET}
gmail.refresh-token=${GMAIL_REFRESH_TOKEN}

# Sender email address
gmail.notification-email=${GMAIL_NOTIFICATION_EMAIL}

# Application name shown in emails
gmail.application-name=Sahabat Quran Web Application
```

### Logging Configuration

```properties
# Adjust Gmail-related logging
logging.level.com.sahabatquran.webapp.service.impl.GmailEmailService=INFO
logging.level.com.sahabatquran.webapp.service.GmailHealthMonitor=INFO
logging.level.com.sahabatquran.webapp.config.GmailConfig=INFO
logging.level.com.google.api.client=WARN
```

## Health Monitoring

The application includes automatic health monitoring to prevent token expiration:

- **Daily health checks** at 2 AM to keep token active
- **Business hours monitoring** (8 AM - 6 PM weekdays)
- **Automatic alerts** after 3 consecutive failures
- **Token keep-alive** prevents 6-month expiration

Monitor health via logs:
```
✅ Gmail service health check passed at 2024-01-15T02:00:00
🔄 Refresh token keep-alive successful - token will remain valid
```

## Testing

### Unit Tests

Run tests without Gmail (uses NoopEmailService):
```bash
mvn test
```

### Integration Tests with Gmail

1. Configure test credentials
2. Run with Gmail profile:
```bash
mvn test -Dspring.profiles.active=gmail \
  -Dgmail.test.recipient.email=test@gmail.com \
  -Dgmail.test.recipient.password=test-password
```

### Manual Testing

1. Send test email via REST endpoint:
```bash
curl -X POST http://localhost:8080/reports/email/send/test-student-id \
  -d "recipientEmail=test@example.com" \
  -d "recipientName=Test User"
```

2. Check application logs for email status
3. Verify email receipt in Gmail inbox

## CI/CD Integration

### GitHub Actions Example

```yaml
env:
  GMAIL_CLIENT_ID: ${{ secrets.GMAIL_CLIENT_ID }}
  GMAIL_CLIENT_SECRET: ${{ secrets.GMAIL_CLIENT_SECRET }}
  GMAIL_REFRESH_TOKEN: ${{ secrets.GMAIL_REFRESH_TOKEN }}
  GMAIL_NOTIFICATION_EMAIL: ${{ secrets.GMAIL_NOTIFICATION_EMAIL }}
  SPRING_PROFILES_ACTIVE: gmail
```

### Docker Configuration

```dockerfile
# In Dockerfile
ENV SPRING_PROFILES_ACTIVE=gmail

# In docker-compose.yml
environment:
  - SPRING_PROFILES_ACTIVE=gmail
  - GMAIL_CLIENT_ID=${GMAIL_CLIENT_ID}
  - GMAIL_CLIENT_SECRET=${GMAIL_CLIENT_SECRET}
  - GMAIL_REFRESH_TOKEN=${GMAIL_REFRESH_TOKEN}
  - GMAIL_NOTIFICATION_EMAIL=${GMAIL_NOTIFICATION_EMAIL}
```

## Troubleshooting

### Token Generator Issues

1. **"ClassNotFoundException" when running token generator**
   ```bash
   # Solution: Ensure dependencies are compiled
   ./mvnw clean compile
   ./mvnw test-compile exec:java \
     -Dexec.mainClass="com.sahabatquran.webapp.util.GmailTokenGenerator" \
     -Dexec.args="/path/to/credentials.json"
   ```

2. **"No plugin found for prefix 'exec'"**
   ```bash
   # Solution: Add exec plugin to pom.xml or use full plugin name
   ./mvnw org.codehaus.mojo:exec-maven-plugin:3.1.0:java \
     -Dexec.mainClass="com.sahabatquran.webapp.util.GmailTokenGenerator" \
     -Dexec.args="/path/to/credentials.json"
   ```

3. **Browser doesn't open automatically**
   - Manually copy the URL printed in console
   - Open in any browser
   - Complete OAuth flow
   - Return to terminal to see the refresh token

### Common Issues

1. **"Gmail service is not initialized"**
   - Check if gmail.enabled=true
   - Verify all credentials are set
   - Check application logs for initialization errors

2. **"401 Unauthorized" errors**
   - Refresh token may be expired (regenerate if unused for 6 months)
   - Credentials may be incorrect
   - OAuth consent screen may need re-approval

3. **"403 Forbidden" errors**
   - Gmail API may not be enabled in Google Cloud Console
   - Scope permissions may be insufficient
   - Daily quota may be exceeded (free tier: 250 quota units/day)

4. **Emails not sending but no errors**
   - Check if using NoopEmailService (gmail.enabled=false)
   - Verify Gmail profile is active
   - Check application logs for NOOP messages

### Debug Mode

Enable detailed logging:
```properties
logging.level.com.sahabatquran.webapp=DEBUG
logging.level.com.google.api.client=DEBUG
```

### Token Expiration

Refresh tokens expire after 6 months of inactivity. The health monitor prevents this, but if expired:

1. Generate new refresh token using GmailTokenGenerator
2. Update configuration with new token
3. Restart application

## Security Best Practices

1. **Never commit credentials** to version control
2. **Use environment variables** in production
3. **Rotate refresh tokens** periodically
4. **Monitor failed authentications** in logs
5. **Use separate Gmail account** for production
6. **Enable 2FA** on the Gmail account
7. **Restrict OAuth app** to specific users in Google Cloud Console
8. **Set up alerts** for unusual activity

## Quota and Limits

Gmail API free tier limits:
- **Daily quota**: 1,000,000,000 quota units
- **Per-user rate limit**: 250 quota units per user per second
- **Send email**: 100 quota units per message
- **Approximate daily emails**: ~10,000 emails/day

## Switching Between Email Services

The application supports seamless switching:

### Use NoopEmailService (Default)
```bash
# No special configuration needed
mvn spring-boot:run
```

### Use GmailEmailService
```bash
mvn spring-boot:run -Dspring.profiles.active=gmail
```

### Runtime Switching

You cannot switch at runtime, but you can:
1. Stop application
2. Change profile
3. Restart application

## Monitoring and Alerts

### Application Metrics

Monitor these key metrics:
- Email send success/failure rate
- Average send time
- Token refresh frequency
- Health check status

### Log Patterns to Watch

Success:
```
✅ Email sent successfully to user@example.com with message ID: xxx
```

Failure:
```
❌ Failed to send student report email to: user@example.com
```

Health:
```
✅ Gmail service health check passed
🚨 CRITICAL: Gmail service has failed 3 consecutive health checks!
```

## Support and Maintenance

### Regular Maintenance Tasks

1. **Monthly**: Review email sending logs
2. **Quarterly**: Rotate refresh tokens
3. **Semi-annually**: Review OAuth app permissions
4. **Annually**: Update Gmail API library versions

### Getting Help

1. Check application logs first
2. Review this documentation
3. Check [Gmail API documentation](https://developers.google.com/gmail/api)
4. Review [OAuth 2.0 documentation](https://developers.google.com/identity/protocols/oauth2)

## Appendix: Complete Setup Checklist

- [ ] Google Cloud Console project created
- [ ] Gmail API enabled
- [ ] OAuth 2.0 credentials created
- [ ] Credentials JSON downloaded
- [ ] Refresh token generated
- [ ] Environment variables configured
- [ ] Application properties updated
- [ ] Gmail profile tested
- [ ] Health monitoring verified
- [ ] Test email sent successfully
- [ ] Production deployment configured
- [ ] Monitoring alerts set up
- [ ] Documentation reviewed with team