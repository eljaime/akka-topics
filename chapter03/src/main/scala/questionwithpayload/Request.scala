package questionwithpayload

case class Request(
    recipientId: String,
    subject: String,
    messageBody: String)
