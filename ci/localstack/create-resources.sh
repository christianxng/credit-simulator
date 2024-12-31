
# AWS SERVICES
echo " =======> verifying email identities...  <======= "
awslocal ses verify-email-identity --email creditteam@email.com
echo " =======> email identities verified...  <======= "

awslocal ses list-identities