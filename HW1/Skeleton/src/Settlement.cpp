#include "Settlement.h"




/// @brief 
/// @return the facility number that allows to build   at a time
int Settlement::getConstructionLimit() const
{
    switch (type) {
        case SettlementType::VILLAGE:
            return 1;  // Village can build 1 facility at a time
        case SettlementType::CITY:
            return 2;  // City can build 2 facilities at a time
        case SettlementType::METROPOLIS:
            return 3;  // Metropolis can build 3 facilities at a time
        default:
            return 0;  // Default case (although this shouldn't happen)
    
}
}

Settlement::Settlement(const string &name, SettlementType type) : name(name), type(type)
{
}

const string &Settlement::getName() const
{
    return name;
}

SettlementType Settlement::getType() const
{
    return type;
}

const string Settlement::toString() const
{
    string typeStr;
    switch (type) {
        case SettlementType::VILLAGE:
            typeStr = "Village";
            break;
        case SettlementType::CITY:
            typeStr = "City";
            break;
        case SettlementType::METROPOLIS:
            typeStr = "Metropolis";
            break;
    }

    return "Settlement Name: " + name + ", Type: " + typeStr;
}
