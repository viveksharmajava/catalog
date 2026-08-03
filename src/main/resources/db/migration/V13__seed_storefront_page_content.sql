-- Seed customer-facing storefront page content for the default product store (OFBIZ_STORE).
-- Content is served to ecart via GET /catalog/storefront/settings and shown on footer pages.

UPDATE product_store_setting SET
    contact_us_content =
'Contact Us

We would love to hear from you. Reach the PlayPro support team using the details below.

Email: playprosportz@gmail.com
Phone: 8431776905

Support hours: Monday–Saturday, 10:00 AM – 6:00 PM IST

You can also use the contact form on this page to send us a message. We typically respond within 1–2 business days.',

    about_us_content =
'About Us

PlayPro delivers performance sports gear to athletes at every level. From weekend players to competitive athletes, we source authentic equipment from leading brands so you can focus on your game.

Our Mission
We make high-quality sports products easy to discover, buy, and receive — with transparent pricing and reliable delivery across India.

Why PlayPro
• 100% authentic products
• Competitive pricing
• Fast and dependable shipping
• Dedicated customer support

Built by sports enthusiasts, for sports enthusiasts.',

    shipping_policy_content =
'Shipping Policy

Delivery timelines
• Standard delivery: 3–7 business days after dispatch
• Express delivery: available in select cities (1–3 business days)

Shipping charges
• Free standard shipping on eligible orders over ₹999
• Express shipping charges (if selected) are shown at checkout

Order processing
Orders are typically processed within 1–2 business days. You will receive updates once your order is packed and handed over to the courier.

Delivery notes
• Please ensure your shipping address and phone number are accurate
• Someone should be available to receive the package
• Delivery timelines may vary for remote locations or peak seasons

For shipping questions, contact us at playprosportz@gmail.com or 8431776905.',

    returns_content =
'Return & Replacement Policy

Important: Sports goods sold on PlayPro are not returnable.

You may raise a replacement request only under the following conditions:
1. Color you don''t like
2. Item delivered in damaged condition

Request window
Replacement requests must be submitted within 7 days of order delivery.

How to raise a replacement request
1. Go to My Account → Orders and open the delivered order
2. Select the item and choose Replacement
3. Share clear photos (for damaged items) and a short description of the issue
4. Our support team will review and confirm next steps

Please note
• Products must be unused and in original packaging wherever applicable
• Replacement is subject to stock availability for the same or equivalent item
• Requests submitted after 7 days of delivery will not be accepted

Need help? Email playprosportz@gmail.com or call 8431776905.',

    privacy_policy_content =
'Privacy Policy

PlayPro ("we", "us") respects your privacy and is committed to protecting your personal information.

Information we collect
We collect information you provide during registration, checkout, and support interactions. This may include your name, email address, phone number, shipping address, and order history.

How we use your information
• To process and deliver your orders
• To provide customer support
• To improve our website and services
• To send order updates and important account communications

We do not sell your personal information to third parties.

Data security
We use reasonable technical and organizational measures to protect your data. Access to personal information is limited to authorized personnel who need it to fulfill their duties.

Your choices
You may request updates to your account details or contact us for privacy-related questions at playprosportz@gmail.com.

Contact
Email: playprosportz@gmail.com
Phone: 8431776905',

    terms_and_conditions_content =
'Terms & Conditions

By using the PlayPro website and placing an order, you agree to these Terms & Conditions.

Eligibility
You must be 18 years or older (or have guardian consent) to make purchases on PlayPro.

Products and pricing
• Product availability and pricing are subject to change without prior notice
• We strive to display accurate product information; minor variations may occur
• All prices are in INR unless otherwise stated

Orders
• Placing an order constitutes an offer to purchase
• We reserve the right to cancel orders in case of pricing errors, stock unavailability, or suspected fraud
• An order confirmation email/SMS does not guarantee fulfillment until the order is dispatched

Shipping and delivery
Delivery timelines and charges are described in our Shipping Policy.

Returns and replacements
Sports goods are not returnable. Replacement requests are allowed only as described in our Return & Replacement Policy, and must be submitted within 7 days of delivery.

Limitation of liability
To the maximum extent permitted by law, PlayPro is not liable for indirect or consequential damages arising from use of the website or purchase of products.

Contact
For questions about these terms, contact playprosportz@gmail.com or 8431776905.',

    last_modified_date = CURRENT_TIMESTAMP
WHERE product_store_id = 'OFBIZ_STORE';
